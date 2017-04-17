package meg.swapout.reporting;

/**
 * Created by margaretmartin on 05/03/2017.
 */
public enum BreakoutLevel {

    Level1("Level 1"),
    Level2("Level 2"),
    All("All");

    private final String displayName;



    BreakoutLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
