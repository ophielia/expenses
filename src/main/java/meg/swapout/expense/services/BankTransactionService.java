package meg.swapout.expense.services;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.RawTransaction;

import java.util.List;

public interface BankTransactionService {
    boolean doesDuplicateExist(RawTransaction trans);

    void addTransaction(RawTransaction trans);

    RawTransaction getById(Long id);

    void saveTransactionAndDetails(RawTransaction rawTransaction, List<CategorizedTransaction> details);

    List<RuleAssignment> getRuleAssignments();

    void updateExpenseByRuleAssignments(List<RuleAssignment> toupdate);
}
