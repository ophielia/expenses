package meg.swapout.expense.services;

import meg.swapout.expense.domain.CategorizedTransaction;
import meg.swapout.expense.domain.RawTransaction;

import java.util.List;

public interface BankTransactionService {
    boolean doesDuplicateExist(RawTransaction trans);

    RawTransaction addTransaction(RawTransaction trans);

    RawTransaction getById(Long id);

    List<RuleAssignment> getRuleAssignments();

    void updateExpenseByRuleAssignments(List<RuleAssignment> toupdate);
}
