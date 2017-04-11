package meg.swapout.expense.services;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.QuickGroup;

import java.util.List;

/**
 * Created by margaretmartin on 12/03/2017.
 */
public interface QuickGroupService {
    List<QuickGroup> listAllQuickGroups();

    QuickGroup getQuickGroupById(Long id);

    QuickGroup saveQuickGroup(QuickGroup quickGroup);

    List<CategorizedTransaction> getExpDetailsForQuickGroup(double amount, Long quickgroupid);
}
