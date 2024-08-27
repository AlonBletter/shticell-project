package dto;

import engine.sheet.effectivevalue.EffectiveValue;

public record CellDTO(
        String originalValue,
        EffectiveValue effectiveValue,
        int lastModifiedVersion
) {
}
