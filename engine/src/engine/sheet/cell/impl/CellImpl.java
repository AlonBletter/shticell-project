package engine.sheet.cell.impl;

import engine.sheet.cell.api.CellStyle;
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
    private CellStyle cellStyle;

    public CellImpl(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.originalValue = "";
        this.effectiveValue = new EffectiveValueImpl(CellType.TEXT, "");
        this.lastModifiedVersion = 1;
        this.cellStyle = new CellStyleImpl();
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
    public CellStyle getStyle() {
        return new CellStyleImpl(cellStyle.getBackgroundColor(), cellStyle.getTextColor());
    }

    @Override
    public void setBackgroundColor(String backgroundColor) {
        cellStyle.setBackgroundColor(backgroundColor);
    }

    @Override
    public void setTextColor(String textColor) {
        cellStyle.setTextColor(textColor);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "originalValue='" + originalValue + '\'' +
                ", effectiveValue='" + effectiveValue + '\'' +
                '}';
    }
}
