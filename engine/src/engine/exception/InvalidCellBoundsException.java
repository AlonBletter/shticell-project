package engine.exception;

import engine.sheet.coordinate.Coordinate;

public class InvalidCellBoundsException extends RuntimeException {
    private final Coordinate actualCoordinate;
    private final int sheetNumOfRows;
    private final int sheetNumOfColumns;

    public InvalidCellBoundsException(Coordinate actualCoordinate, int sheetNumOfRows, int sheetNumOfColumns) {
        this.actualCoordinate = actualCoordinate;
        this.sheetNumOfRows = sheetNumOfRows;
        this.sheetNumOfColumns = sheetNumOfColumns;
    }

    public InvalidCellBoundsException(Coordinate actualCoordinate, int sheetNumOfRows, int sheetNumOfColumns, String message) {
        super(message);
        this.actualCoordinate = actualCoordinate;
        this.sheetNumOfRows = sheetNumOfRows;
        this.sheetNumOfColumns = sheetNumOfColumns;
    }

    public Coordinate getActualCoordinate() {
        return actualCoordinate;
    }

    public int getSheetNumOfRows() {
        return sheetNumOfRows;
    }

    public int getSheetNumOfColumns() {
        return sheetNumOfColumns;
    }
}

