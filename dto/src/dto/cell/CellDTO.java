package dto.cell;

import dto.coordinate.Coordinate;
import dto.effectivevalue.EffectiveValue;

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
