package meg.swapout.expense.services;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.RawTransaction;

import java.util.List;

/**
 * Created by margaretmartin on 09/04/2017.
 */
public interface TransactionDetailService {
    List<CategorizedTransaction> listAllCategorizedTransactions();

    CategorizedTransaction getCategorizedTransactionById(Long id);

    List<CategorizedTransaction> saveDetailsForTransaction(RawTransaction transaction, List<CategorizedTransaction> details);

    void deleteCategorizedTransaction(Long id);

    List<CategorizedTransaction> distributeAmount(RawTransaction transaction, List<CategorizedTransaction> details, boolean remainderOnly);
}
