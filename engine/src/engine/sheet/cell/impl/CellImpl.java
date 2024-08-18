package engine.sheet.cell.impl;

import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.cell.api.Cell;
import engine.sheet.impl.EffectiveValueImpl;

public class CellImpl implements Cell {
    private String originalValue;
    private EffectiveValue effectiveValue;

    public CellImpl(String originalValue, EffectiveValue effectiveValue) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
    }

    public CellImpl() {
        this.originalValue = "";
        this.effectiveValue = new EffectiveValueImpl(CellType.TEXT, "");
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public void setCellOriginalValue(String value) {
        this.originalValue = value;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    @Override
    public void setEffectiveValue(EffectiveValue value) {
        this.effectiveValue = value;
    }
}
