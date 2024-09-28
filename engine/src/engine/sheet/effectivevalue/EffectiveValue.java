package engine.sheet.effectivevalue;

import engine.sheet.cell.api.CellType;

public interface EffectiveValue {
    CellType cellType();
    Object value();
    <T> T extractValueWithExpectation(Class<T> type);
    String extractStringValue();
}
