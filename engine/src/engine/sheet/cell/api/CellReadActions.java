package engine.sheet.cell.api;

import engine.sheet.effectivevalue.EffectiveValue;

public interface CellReadActions {
    String getOriginalValue();
    EffectiveValue getEffectiveValue();
    int getLastModifiedVersion();
}
