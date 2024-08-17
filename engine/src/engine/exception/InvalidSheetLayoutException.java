package engine.exception;

public class InvalidSheetLayoutException extends RuntimeException {
    private final int actualRows;
    private final int actualColumns;
    private final int maxRows;
    private final int maxColumns;

    public InvalidSheetLayoutException(String message, int actualRows, int actualColumns, int maxRows, int maxColumns) {
        super(message);
        this.actualRows = actualRows;
        this.actualColumns = actualColumns;
        this.maxRows = maxRows;
        this.maxColumns = maxColumns;
    }

    public InvalidSheetLayoutException(int actualRows, int actualColumns, int maxRows, int maxColumns) {
        this.actualRows = actualRows;
        this.actualColumns = actualColumns;
        this.maxRows = maxRows;
        this.maxColumns = maxColumns;
    }

    public int getActualRows() {
        return actualRows;
    }

    public int getActualColumns() {
        return actualColumns;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public int getMaxColumns() {
        return maxColumns;
    }
}

