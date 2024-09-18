package dto;

import engine.sheet.cell.api.CellStyle;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;

public record CellDTO(
        Coordinate coordinate,
        String originalValue,
        EffectiveValue effectiveValue,
        int lastModifiedVersion,
        CellStyle cellStyle
) {
}
