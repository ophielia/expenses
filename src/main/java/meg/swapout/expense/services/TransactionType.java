package meg.swapout.expense.services;

/**
 * Created by margaretmartin on 05/03/2017.
 */
public enum TransactionType {

    Debits("Debits"),
    Credits("Credits"),
    All("All");

    private final String displayName;



    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
