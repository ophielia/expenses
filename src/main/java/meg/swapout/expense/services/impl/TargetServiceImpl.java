package meg.swapout.expense.services.impl;

import meg.swapout.expense.domain.Target;
import meg.swapout.expense.domain.TargetDetail;
import meg.swapout.expense.repositories.TargetDetailRepository;
import meg.swapout.expense.repositories.TargetRepository;
import meg.swapout.expense.services.TargetService;
import meg.swapout.expense.services.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by margaretmartin on 05/03/2017.
 */
@Service
public class TargetServiceImpl implements TargetService {

    @Autowired
    TargetRepository targetRepository;

    @Autowired
    TargetDetailRepository targetDetailRepository;
    
    @Override
    public List<Target> listAllTargets(TargetType targetType) {
        return targetRepository.findTargetsByType(targetType);
    }

    @Override
    public Target getTargetById(Long id) {
        return targetRepository.findOne(id);
    }

    @Override

    @Transactional
    public Target saveTarget(Target target) {
        if (target != null) {

            // save and return right away if this is new
            if (target.getId() == null) {
                return saveTargetWithDetails(target);
            }

            // get quick group from db
            Target dbGroup = targetRepository.findOne(target.getId());

            // squash details from db quickgroup
            HashMap<Long, TargetDetail> dbMap = squashDetails(dbGroup.getTargetdetails());


            // squash details from passed quick group
            HashMap<Long, TargetDetail> newMap = squashDetails(target.getTargetdetails());

            // go through new details
            List<TargetDetail> toDelete = new ArrayList<>();
            List<TargetDetail> toSave = new ArrayList<>();
            for (Long catId : newMap.keySet()) {
                TargetDetail newDetail = newMap.get(catId);
                if (dbMap.containsKey(catId)) {
                    TargetDetail dbDetail = dbMap.get(catId);
                    dbDetail.setAmount(newDetail.getAmount());
                    toSave.add(dbDetail);
                    dbMap.remove(catId);
                } else {
                    toSave.add(newDetail);
                }

            }
            // delete details to be deleted
            if (dbGroup.getTargetdetails()!=null) {
                for (TargetDetail detail: dbGroup.getTargetdetails()) {
                    if (toSave.contains(detail)) continue;
                    toDelete.add(detail);
                }
            }
            if (toDelete.size() > 0) {
                targetDetailRepository.delete(toDelete);
            }

            // set details in object
            target.setTargetdetails(toSave);

            // save quick group and object
            return saveTargetWithDetails(target);
        }


        return null;
    }

    private HashMap<Long, TargetDetail> squashDetails(List<TargetDetail> details) {
        HashMap<Long, TargetDetail> map = new HashMap<>();
        if (details != null) {
            for (TargetDetail detail : details) {
                Long catId = detail.getCategory().getId();
                if (map.containsKey(catId)) {
                    TargetDetail mapDetail = map.get(catId);
                    // perform swap if necessary
                    TargetDetail toSaveInHash = detail.getId() != null ? detail : mapDetail;
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

    private Target saveTargetWithDetails(Target target) {
        // set quick group in details
        List<TargetDetail> details = target.getTargetdetails();
        if (details != null) {
            for (TargetDetail detail : details) {
                if (detail.getTargetgroup() == null) {
                    detail.setTargetgroup(target);
                }
            }
            target.setTargetdetails(details);
        }

        // save quick group
        return targetRepository.save(target);
    }


    @Override
    public void deleteTarget(Long id) {
        Target toDelete = getTargetById(id);
        if (toDelete!=null) {
            targetRepository.delete(toDelete);
        }
    }
}
