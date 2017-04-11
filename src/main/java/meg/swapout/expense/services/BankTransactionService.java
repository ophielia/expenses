package meg.swapout.expense.services;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.RawTransaction;

import java.util.List;

/**
 * Created by margaretmartin on 26/03/2017.
 */
public interface BankTransactionService {
    boolean doesDuplicateExist(RawTransaction trans);

    void addTransaction(RawTransaction trans);

    void assignCategoriesToExpenses(Category batchUpdate, List<String> toupdate);

    void assignQuickGroupToExpenses(Long batchQuickgroup, List<String> toupdate);

    List<CategorizedTransaction> getCategoryExpForTrans(Long transid);

    void assignCategory(Long transid, Category category);

    void saveTransactionAndDetails(RawTransaction rawTransaction, List<CategorizedTransaction> details);
}
