package engine.sheet.cell.impl;

import dto.CellDTO;
import engine.sheet.cell.api.Cell;
import engine.sheet.api.EffectiveValue;

public class CellImpl implements Cell {
    private final String originalValue;
    private EffectiveValue effectiveValue;
    private int row;
    private String column;

    private CellImpl(String originalValue) {
        this.originalValue = originalValue;
    }

    public static CellImpl createCell(String originalValue) {

        return new CellImpl(originalValue);
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    public CellDTO getCell() {

        return new CellDTO();
    }
}
