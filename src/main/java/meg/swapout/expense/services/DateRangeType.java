package meg.swapout.expense.services;

/**
 * Created by margaretmartin on 05/03/2017.
 */
public enum DateRangeType {

    CurrentMonth("Current Month"),
    LastMonth("Last Month"),
    MonthBeforeLast("Month Before Last"),
    All("All"),
    ThisWeek("This Week"),
    LastWeek("Last Week"),
    CurrentYear("Current Year"), LastYear("Last Year");

    private final String displayName;



    DateRangeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
