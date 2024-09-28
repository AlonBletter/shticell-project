package dto;

import engine.sheet.cell.api.CellStyle;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;

import java.util.List;

public record CellDTO(
        Coordinate coordinate,
        String originalValue,
        EffectiveValue effectiveValue,
        int lastModifiedVersion,
        List<Coordinate> dependsOn,
        List<Coordinate> influenceOn,
        CellStyle cellStyle,
        boolean containsFunction
) {
}
