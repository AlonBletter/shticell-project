package engine.sheet.cell.impl;

import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.cell.api.Cell;
import engine.sheet.effectivevalue.EffectiveValueImpl;

import java.io.Serializable;

public class CellImpl implements Cell, Serializable {
    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int lastModifiedVersion;

    public CellImpl(Coordinate coordinate, String originalValue, EffectiveValue effectiveValue, int lastModifiedVersion) {
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
    }

    public CellImpl(Coordinate coordinate) {
        this.coordinate = coordinate;
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
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "originalValue='" + originalValue + '\'' +
                ", effectiveValue='" + effectiveValue + '\'' +
                '}';
    }
}
