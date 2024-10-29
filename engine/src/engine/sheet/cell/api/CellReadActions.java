package engine.sheet.cell.api;

import dto.coordinate.Coordinate;
import dto.effectivevalue.EffectiveValue;

import java.util.List;

public interface CellReadActions {
    String getOriginalValue();
    EffectiveValue getEffectiveValue();
    int getLastModifiedVersion();
    String getLastModifiedBy();
    Coordinate getCoordinate();
    CellStyle getStyle();
    List<Coordinate> getDependsOn();
    List<Coordinate> getInfluenceOn();
    boolean isContainFunction();
}
