package meg.swapout.expense.services;

import meg.swapout.expense.domain.Target;

import java.util.List;

/**
 * Created by margaretmartin on 05/03/2017.
 */
public interface TargetService {


    List<Target> listAllTargets(TargetType monthly);

    Target getTargetById(Long id);

    Target saveTarget(Target target);

    void deleteTarget(Long id);

    Target loadTargetForMonth(String month);

    Target loadTargetForYear(String year);
}
