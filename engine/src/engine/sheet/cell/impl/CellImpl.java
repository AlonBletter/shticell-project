package engine.sheet.cell.impl;

import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.cell.api.Cell;
import engine.sheet.impl.EffectiveValueImpl;

import java.io.Serializable;

public class CellImpl implements Cell, Serializable {
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int lastModifiedVersion;

    public CellImpl(String originalValue, EffectiveValue effectiveValue, int lastModifiedVersion) {
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
    }

    public CellImpl() {
        this.originalValue = "";
        this.effectiveValue = new EffectiveValueImpl(CellType.TEXT, "");
        this.lastModifiedVersion = 1;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    @Override
    public void setOriginalValue(String value) {
        this.originalValue = value;
    }

    @Override
    public void setEffectiveValue(EffectiveValue value) {
        this.effectiveValue = value;
    }

    @Override
    public void setLastModifiedVersion(int value) {
        this.lastModifiedVersion = value;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "originalValue='" + originalValue + '\'' +
                ", effectiveValue='" + effectiveValue + '\'' +
                '}';
    }
}
