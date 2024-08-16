package engine.sheet.cell.api;

import engine.sheet.api.EffectiveValue;

public interface Cell {
    String getOriginalValue();
    EffectiveValue getEffectiveValue();
}
