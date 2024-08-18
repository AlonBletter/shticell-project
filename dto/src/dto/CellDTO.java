package dto;

import engine.sheet.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;

public record CellDTO(
        String originalValue,
        EffectiveValue effectiveValue
) {
}
