package meg.swapout.expense.services;

/**
 * Created by margaretmartin on 05/03/2017.
 */
public enum CategorizedType {

    NonCategorized("Non-Categorized"),
            OnlyCategorized("Categorized Only"),
            All("All");
    private final String displayName;



    CategorizedType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
