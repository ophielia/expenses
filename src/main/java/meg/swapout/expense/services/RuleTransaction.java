package meg.swapout.expense.services;

import meg.swapout.expense.domain.RawTransaction;

public class RuleTransaction {

private final RawTransaction transaction;
private int count;

    public RuleTransaction(RawTransaction transaction) {
        this.transaction = transaction;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public RawTransaction getTransaction() {
        return transaction;
    }
}
