package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.RawTransaction;
import meg.swapout.expense.repositories.CategorizedTransactionRepository;
import meg.swapout.expense.repositories.RawTransactionRepository;
import meg.swapout.expense.services.TransactionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by margaretmartin on 05/03/2017.
 */
@Service
public class TransactionDetailServiceImpl implements TransactionDetailService {

    @Autowired
    CategorizedTransactionRepository categorizedTransactionRepository;

    @Autowired
    RawTransactionRepository rawTransactionRepo;


    @Override
    public List<CategorizedTransaction> listAllCategorizedTransactions() {
        return categorizedTransactionRepository.findAll();
    }

    @Override
    public CategorizedTransaction getCategorizedTransactionById(Long id) {
        return categorizedTransactionRepository.findOne(id);
    }

    @Override
    public List<CategorizedTransaction> saveDetailsForTransaction(RawTransaction transaction, List<CategorizedTransaction> details) {
        if (transaction != null) {

            // get details from db
            List<CategorizedTransaction> dbdetails = categorizedTransactionRepository.findByBankTrans(transaction);

            // squash details from db
            HashMap<Long, CategorizedTransaction> dbMap = squashDetails(dbdetails);


            // squash details from passed quick group
            HashMap<Long, CategorizedTransaction> newMap = squashDetails(details);

            // go through new details
            List<CategorizedTransaction> toDelete = new ArrayList<>();
            List<CategorizedTransaction> toSave = new ArrayList<>();
            for (Long catId : newMap.keySet()) {
                CategorizedTransaction newDetail = newMap.get(catId);
                if (dbMap.containsKey(catId)) {
                    CategorizedTransaction dbDetail = dbMap.get(catId);
                    dbDetail.setAmount(newDetail.getAmount());
                    toSave.add(dbDetail);
                    dbMap.remove(catId);
                } else {
                    toSave.add(newDetail);
                }

            }
            // delete details to be deleted
            if (dbdetails != null) {
                for (CategorizedTransaction detail : dbdetails) {
                    if (toSave.contains(detail)) continue;
                    toDelete.add(detail);
                }
            }
            if (toDelete.size() > 0) {
                categorizedTransactionRepository.delete(toDelete);
            }


            // save quick group and object
            return saveCategorizedTransactionList(transaction, toSave);
        }


        return null;
    }

    private List<CategorizedTransaction> saveCategorizedTransactionList(RawTransaction transaction, List<CategorizedTransaction> details) {
        for (CategorizedTransaction detail : details) {
            detail.setBanktrans(transaction);
        }
        return categorizedTransactionRepository.save(details);
    }

    private HashMap<Long, CategorizedTransaction> squashDetails(List<CategorizedTransaction> details) {
        HashMap<Long, CategorizedTransaction> map = new HashMap<>();
        if (details != null) {
            for (CategorizedTransaction detail : details) {
                Long catId = detail.getCategory().getId();
                if (map.containsKey(catId)) {
                    CategorizedTransaction mapDetail = map.get(catId);
                    // perform swap if necessary
                    CategorizedTransaction toSaveInHash = detail.getId() != null ? detail : mapDetail;
                    Double percentage = mapDetail.getAmount() != null ? mapDetail.getAmount() : 0D;
                    percentage += detail.getAmount() != null ? detail.getAmount() : 0D;
                    toSaveInHash.setAmount(percentage);
                    map.put(catId, toSaveInHash);
                } else {
                    map.put(catId, detail);
                }
            }
        }
        return map;
    }

    @Override
    public void deleteCategorizedTransaction(Long id) {
        CategorizedTransaction toDelete = getCategorizedTransactionById(id);
        if (toDelete != null) {
            categorizedTransactionRepository.delete(toDelete);
        }
    }

    @Override
    public List<CategorizedTransaction> distributeAmount(RawTransaction transaction, List<CategorizedTransaction> details, boolean remainderOnly) {
        // determine amount to be distributed
        Double amountToDistribute = getRemainder(transaction, details, remainderOnly);
        if (amountToDistribute == null) {
            return null;
        }

        // find count of details in which to distribute
        int distributeCount = remainderOnly ? getDistributeCount(details) : details.size();

        // calculate distribute amount
        Double amountPerDetail = Math.round(amountToDistribute * 100.0 / (double) distributeCount) / 100.00;

        // get last amount
        Double lastAmount = amountPerDetail;
        if ((distributeCount * amountPerDetail) != amountToDistribute) {
            lastAmount = lastAmount + (amountToDistribute - (distributeCount * amountPerDetail));
        }

        // set amounts in details
        List<CategorizedTransaction> results = new ArrayList<>();
        int count = 0;
        for (CategorizedTransaction detail : details) {
            count++;
            if (remainderOnly && detail.getAmount() != null && detail.getAmount() != 0.0) {
                results.add(detail);
                continue;
            }
            if (count == distributeCount) {
                detail.setAmount(lastAmount);
            } else {
                detail.setAmount(amountPerDetail);
            }
            results.add(detail);

        }
        // return details list
        return details;
    }

    private int getDistributeCount(List<CategorizedTransaction> details) {
        if (details == null) {
            return 1;
        }
        int count = 0;
        for (CategorizedTransaction detail : details) {
            if (detail.getAmount() == null || detail.getAmount() == 0D) {
                count++;
            }
        }
        return count;
    }

    private Double getRemainder(RawTransaction transaction, List<CategorizedTransaction> details, boolean remainderOnly) {
        if (transaction == null || details == null) {
            return null;
        }

        Double transamount = transaction.getAmount();
        if (!remainderOnly) {
            return transamount;
        }
        Double sumofparts = 0D;
        for (CategorizedTransaction detail : details) {
            sumofparts += detail.getAmount() != null ? detail.getAmount() : 0D;
        }

        return transamount - sumofparts;
    }
}
