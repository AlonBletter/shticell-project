package dto.effectivevalue;

import dto.cell.CellType;

public interface EffectiveValue {
    CellType cellType();
    Object value();
    <T> T extractValueWithExpectation(Class<T> type);
    String extractStringValue();
}
