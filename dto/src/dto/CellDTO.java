package dto;

import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;

public record CellDTO(
        Coordinate coordinate,
        String originalValue,
        EffectiveValue effectiveValue,
        int lastModifiedVersion
) {
}
