package engine.sheet.cell.api;

import engine.sheet.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;

import java.util.List;

public interface Cell {
    String getOriginalValue();
    void setOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    void setEffectiveValue(EffectiveValue value);
    int getLastModifiedVersion();
    void setLastModifiedVersion(int value);
}
