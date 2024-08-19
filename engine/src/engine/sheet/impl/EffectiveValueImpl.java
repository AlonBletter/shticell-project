package engine.sheet.impl;

import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;

public class EffectiveValueImpl implements EffectiveValue {

    private CellType cellType;
    private Object value;

    public EffectiveValueImpl(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }
        //TODO: error handling... exception ? return null ?
        return null;
    }

    @Override
    public String toString() {
        return "EffectiveValueImpl{" +
                "cellType=" + cellType +
                ", value=" + value +
                '}';
    }
}
