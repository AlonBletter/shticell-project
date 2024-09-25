package engine.sheet.effectivevalue;

import engine.sheet.cell.api.CellType;

import java.io.Serializable;
import java.util.Objects;

public class EffectiveValueImpl implements EffectiveValue, Serializable {

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

        return null;
    }


    //TODO remove later if time...
    @Override
    public String extractStringValue() {
        String formattedObject;

        if (CellType.NUMERIC == cellType) {
            double doubleValue = (Double) value;

            if (doubleValue % 1 == 0) {
                long longValue = (long) doubleValue;

                formattedObject = String.format("%,d", longValue);
            } else {
                formattedObject = String.format("%,.2f", doubleValue);
            }
        } else if (CellType.BOOLEAN == cellType) {
            boolean booleanValue = (Boolean) value;

            formattedObject = Boolean.toString(booleanValue).toUpperCase();
        } else if (CellType.EMPTY == value) {
            formattedObject = "";
        } else {
            formattedObject = value.toString();
        }

        return formattedObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectiveValueImpl that = (EffectiveValueImpl) o;
        return cellType == that.cellType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType, value);
    }

    @Override
    public String toString() {
        return "EffectiveValueImpl{" +
                "cellType=" + cellType +
                ", value=" + value +
                '}';
    }
}
