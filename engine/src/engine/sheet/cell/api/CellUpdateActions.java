package engine.sheet.cell.api;

import dto.coordinate.Coordinate;
import dto.effectivevalue.EffectiveValue;

import java.util.List;

public interface CellUpdateActions {
    void setOriginalValue(String value);
    void setEffectiveValue(EffectiveValue value);
    void setLastModifiedVersion(int value);
    void setLastModifiedBy(String lastModifiedBy);
    void setBackgroundColor(String backgroundColor);
    void setTextColor(String textColor);
    void setDependsOn(List<Coordinate> dependsOn);
    void setInfluenceOn(List<Coordinate> influenceOn);
}
