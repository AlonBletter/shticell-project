package engine.sheet.cell.api;

import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;

import java.util.List;

public interface CellReadActions {
    String getOriginalValue();
    EffectiveValue getEffectiveValue();
    int getLastModifiedVersion();
    Coordinate getCoordinate();
    CellStyle getStyle();
    List<Coordinate> getDependsOn();
    List<Coordinate> getInfluenceOn();
}
