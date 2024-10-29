package engine.sheet.cell.impl;

import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.CellStyle;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.effectivevalue.EffectiveValueImpl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CellImpl implements Cell, Serializable {
    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int lastModifiedVersion;
    private String lastModifiedBy;
    private List<Coordinate> dependsOn;
    private List<Coordinate> influenceOn;
    private final CellStyle cellStyle;

    public CellImpl(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.originalValue = "";
        this.effectiveValue = new EffectiveValueImpl(CellType.TEXT, "");
        this.lastModifiedVersion = 1;
        this.cellStyle = new CellStyleImpl();
        this.dependsOn = new LinkedList<>();
        this.influenceOn = new LinkedList<>();
    }

    public List<Coordinate> getDependsOn() {
        return new LinkedList<>(dependsOn);
    }

    public void setDependsOn(List<Coordinate> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public List<Coordinate> getInfluenceOn() {
        return new LinkedList<>(influenceOn);
    }

    public void setInfluenceOn(List<Coordinate> influenceOn) {
        this.influenceOn = influenceOn;
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
    public boolean isContainFunction() {
        return originalValue.startsWith("{") && originalValue.endsWith("}");
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public CellStyle getStyle() {
        return cellStyle;
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
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "originalValue='" + originalValue + '\'' +
                ", effectiveValue='" + effectiveValue + '\'' +
                '}';
    }
}
