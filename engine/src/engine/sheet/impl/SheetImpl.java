package engine.sheet.impl;

import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;

public class SheetImpl implements Sheet {

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public Cell getCell(int row, int column) {
        return null;
    }

    @Override
    public void setCell(int row, int column, String value) {

    }
}
