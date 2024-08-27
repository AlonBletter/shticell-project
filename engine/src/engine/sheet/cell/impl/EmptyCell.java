package engine.sheet.cell.impl;

import engine.sheet.cell.api.CellType;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.cell.api.Cell;
import engine.sheet.effectivevalue.EffectiveValueImpl;

import java.io.Serializable;

public enum EmptyCell implements Cell, Serializable {
    INSTANCE;

    @Override
    public String getOriginalValue() {
        return "";
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return new EffectiveValueImpl(CellType.EMPTY, ""); //TODO Maybe null
    }

    @Override
    public int getLastModifiedVersion() {
        return 0;
    }

    @Override
    public void setOriginalValue(String value) {
        throw new UnsupportedOperationException("Cannot modify an empty cell.");
    }

    @Override
    public void setEffectiveValue(EffectiveValue value) {
        throw new UnsupportedOperationException("Cannot modify an empty cell.");
    }

    @Override
    public void setLastModifiedVersion(int value) {
        throw new UnsupportedOperationException("Cannot modify an empty cell.");
    }

    @Override
    public String toString() {
        return "EmptyCell{}";
    }
}