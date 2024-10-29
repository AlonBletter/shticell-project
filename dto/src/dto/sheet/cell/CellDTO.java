package dto.sheet.cell;

import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;

import java.util.List;

public record CellDTO(
        Coordinate coordinate,
        String originalValue,
        EffectiveValue effectiveValue,
        int lastModifiedVersion,
        String modifiedBy,
        List<Coordinate> dependsOn,
        List<Coordinate> influenceOn,
        CellStyleDTO cellStyle,
        boolean containsFunction
) {
}
