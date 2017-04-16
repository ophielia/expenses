package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.QuickGroupDetail;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.repositories.QuickGroupDetailRepository;
import meg.swapout.expense.repositories.QuickGroupRepository;
import meg.swapout.expense.services.BankTransactionService;
import meg.swapout.expense.services.QuickGroupService;
import meg.swapout.expense.services.TransactionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by margaretmartin on 12/03/2017.
 */
@SuppressWarnings({"DefaultFileTemplate", "SpringAutowiredFieldsWarningInspection"})
@Service
public class QuickGroupServiceImpl implements QuickGroupService {

    @Autowired
    private
    QuickGroupRepository quickGroupRepository;

    @Autowired
    BankTransactionService bankTransactionService;

    @Autowired
    TransactionDetailService transactionDetailService;

    @Autowired
    private
    QuickGroupDetailRepository quickGroupDetailRepository;

    @Override
    public List<QuickGroup> listAllQuickGroups() {
        return quickGroupRepository.findAll();
    }

    @Override
    public QuickGroup getQuickGroupById(Long id) {
        return quickGroupRepository.findOne(id);
    }

    @Override
    @Transactional
    public QuickGroup saveQuickGroup(QuickGroup quickGroup) {
        if (quickGroup != null) {

            // save and return right away if this is new
            if (quickGroup.getId() == null) {
                return saveQuickGroupWithDetails(quickGroup);
            }

            // get quick group from db
            QuickGroup dbGroup = quickGroupRepository.findOne(quickGroup.getId());

            // squash details from db quickgroup
            HashMap<Long, QuickGroupDetail> dbMap = squashDetails(dbGroup.getGroupdetails());


            // squash details from passed quick group
            HashMap<Long, QuickGroupDetail> newMap = squashDetails(quickGroup.getGroupdetails());

            // go through new details
            List<QuickGroupDetail> toDelete = new ArrayList<>();
            List<QuickGroupDetail> toSave = new ArrayList<>();
            for (Long catId : newMap.keySet()) {
                QuickGroupDetail newDetail = newMap.get(catId);
                if (dbMap.containsKey(catId)) {
                    QuickGroupDetail dbDetail = dbMap.get(catId);
                    dbDetail.setPercentage(newDetail.getPercentage());
                    toSave.add(dbDetail);
                    dbMap.remove(catId);
                } else {
                    toSave.add(newDetail);
                }

            }
            // delete details to be deleted
            if (dbGroup.getGroupdetails()!=null) {
                for (QuickGroupDetail detail: dbGroup.getGroupdetails()) {
                    if (toSave.contains(detail)) continue;
                    toDelete.add(detail);
                }
            }
            if (toDelete.size() > 0) {
                quickGroupDetailRepository.delete(toDelete);
            }

            // set details in object
            quickGroup.setGroupdetails(toSave);

            // save quick group and object
            return saveQuickGroupWithDetails(quickGroup);
        }


        return null;
    }

    @Override
    public QuickGroup createQuickGroupFromTransactionDetails(Long transactionId) {
        // get transaction
        RawTransaction transaction = bankTransactionService.getById(transactionId);
        if (transaction == null) {
            return null;
        }

        // get details
        List<CategorizedTransaction> details = transactionDetailService.getDetailsForTransaction(transaction);
        if (details == null) {
            return null;
        }

        // create quick group with default name
        QuickGroup quickGroup = new QuickGroup();
        quickGroup = saveQuickGroup(quickGroup);

        // create empty list of QuickGroupDetails, total field
        List<QuickGroupDetail> groupDetails = new ArrayList<>();
        Double total = 0D;
        Double transactionTotal = transaction.getAmount();
        // cycle through transaction details, creating quickgroupdetail for each one
        for (CategorizedTransaction detail:details) {
            Double amount = detail.getAmount();
            total+=amount;
            Double percentage = Math.round(amount * 10000D/ transactionTotal)/100D;

            QuickGroupDetail quickGroupDetail = new QuickGroupDetail();
            quickGroupDetail.setCategory(detail.getCategory());
            quickGroupDetail.setQuickgroup(quickGroup);
            quickGroupDetail.setPercentage(percentage);
            groupDetails.add(quickGroupDetail);
        }
        // make adjustment, if necessary
        if (total.doubleValue() != 100D) {
            Double adjustment = 100D - total;
            QuickGroupDetail quickGroupDetail = groupDetails.get(groupDetails.size()-1);
            Double amount = quickGroupDetail.getPercentage();
            quickGroupDetail.setPercentage(amount + adjustment);
            groupDetails.set(groupDetails.size()-1,quickGroupDetail);
        }

        // place details in quickgroup
        quickGroup.setGroupdetails(groupDetails);

        // save quick group
        quickGroup = saveQuickGroupWithDetails(quickGroup);


        return quickGroup;
    }

    private HashMap<Long, QuickGroupDetail> squashDetails(List<QuickGroupDetail> details) {
        HashMap<Long, QuickGroupDetail> map = new HashMap<>();
        if (details != null) {
            for (QuickGroupDetail detail : details) {
                Long catId = detail.getCategory().getId();
                if (map.containsKey(catId)) {
                    QuickGroupDetail mapDetail = map.get(catId);
                    // perform swap if necessary
                    QuickGroupDetail toSaveInHash = detail.getId() != null ? detail : mapDetail;
                    Double percentage = mapDetail.getPercentage() != null ? mapDetail.getPercentage() : 0D;
                    percentage += detail.getPercentage() != null ? detail.getPercentage() : 0D;
                    toSaveInHash.setPercentage(percentage);
                    map.put(catId, toSaveInHash);
                } else {
                    map.put(catId, detail);
                }
            }
        }
        return map;
    }

    private QuickGroup saveQuickGroupWithDetails(QuickGroup quickGroup) {
        // set quick group in details
        List<QuickGroupDetail> details = quickGroup.getGroupdetails();
        if (details != null) {
            for (QuickGroupDetail detail : details) {
                if (detail.getQuickgroup() == null) {
                    detail.setQuickgroup(quickGroup);
                }
            }
            quickGroup.setGroupdetails(details);
        }

        // save quick group
        return quickGroupRepository.save(quickGroup);
    }


}
