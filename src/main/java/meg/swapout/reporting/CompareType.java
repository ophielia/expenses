package meg.swapout.reporting;

/**
 * Created by margaretmartin on 05/03/2017.
 */
public enum CompareType {

    LastMonths("Last 12 Months"),
    CalendarYear("Last Calendar Year"),
    All("All");

    private final String displayName;



    CompareType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
