package meg.swapout.expense.services;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.Category;
import meg.swapout.expense.domain.QuickGroup;
import meg.swapout.expense.domain.RawTransaction;

import java.util.List;

/**
 * Created by margaretmartin on 09/04/2017.
 */
public interface TransactionDetailService {
    List<CategorizedTransaction> listAllCategorizedTransactions();

    CategorizedTransaction getCategorizedTransactionById(Long id);

    List<CategorizedTransaction> saveDetailsAndTransaction(RawTransaction transaction, List<CategorizedTransaction> details);

    List<CategorizedTransaction> distributeAmount(RawTransaction transaction, List<CategorizedTransaction> details, boolean remainderOnly);

    List<CategorizedTransaction> addQuickGroupToDetails(RawTransaction transaction, List<CategorizedTransaction> details, QuickGroup quickGroup, boolean addOnly);

    void assignCategoriesToTransactionDetails(Category batchUpdate, List<String> toupdate);

    void assignQuickGroupToTransactionDetails(QuickGroup batchQuickgroup, List<String> toupdate);

    List<CategorizedTransaction> getDetailsForTransaction(RawTransaction transaction);

    void assignCategoryToTransaction(Long transid, Category cat);

    void roundTransactions(List<String> toupdate);
}
