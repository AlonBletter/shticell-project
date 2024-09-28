package engine.sheet.cell.impl;

import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.CellStyle;
import engine.sheet.cell.api.CellType;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.effectivevalue.EffectiveValueImpl;

import java.io.Serializable;
import java.util.List;

public enum EmptyCell implements Cell, Serializable {
    INSTANCE;

    @Override
    public String getOriginalValue() {
        return "";
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return new EffectiveValueImpl(CellType.EMPTY, "");
    }

    @Override
    public int getLastModifiedVersion() {
        throw new UnsupportedOperationException("Empty cell has no information.");
    }

    @Override
    public Coordinate getCoordinate() {
        throw new UnsupportedOperationException("Empty cell has no information.");
    }

    @Override
    public CellStyle getStyle() {
        throw new UnsupportedOperationException("Empty cell has no information.");
    }

    @Override
    public List<Coordinate> getDependsOn() {
        throw new UnsupportedOperationException("Empty cell has no information.");
    }

    @Override
    public List<Coordinate> getInfluenceOn() {
        throw new UnsupportedOperationException("Empty cell has no information.");
    }

    @Override
    public boolean isContainFunction() {
        throw new UnsupportedOperationException("Empty cell has no information.");
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
    public void setBackgroundColor(String backgroundColor) {
        throw new UnsupportedOperationException("Cannot modify an empty cell.");
    }

    @Override
    public void setTextColor(String textColor) {
        throw new UnsupportedOperationException("Cannot modify an empty cell.");
    }

    @Override
    public void setDependsOn(List<Coordinate> dependsOn) {
        throw new UnsupportedOperationException("Cannot modify an empty cell.");
    }

    @Override
    public void setInfluenceOn(List<Coordinate> influenceOn) {
        throw new UnsupportedOperationException("Cannot modify an empty cell.");
    }


    @Override
    public String toString() {
        return "EmptyCell{}";
    }
}