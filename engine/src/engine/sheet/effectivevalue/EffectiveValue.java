package engine.sheet.effectivevalue;

import engine.sheet.cell.api.CellType;

public interface EffectiveValue {
    CellType getCellType();
    Object getValue();
    <T> T extractValueWithExpectation(Class<T> type);
}
