package engine.sheet.cell.api;

import engine.sheet.effectivevalue.EffectiveValue;

public interface CellUpdateActions {
    void setOriginalValue(String value);
    void setEffectiveValue(EffectiveValue value);
    void setLastModifiedVersion(int value);
}
