package engine.sheet.cell.api;

import engine.sheet.api.EffectiveValue;

public interface CellReadActions {
    String getOriginalValue();
    EffectiveValue getEffectiveValue();
    int getLastModifiedVersion();
}
