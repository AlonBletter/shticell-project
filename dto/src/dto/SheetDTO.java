package dto;

import engine.sheet.coordinate.*;

import java.util.List;
import java.util.Map;

public record SheetDTO(
        String name,
        int numOfRows,
        int numOfColumns,
        int rowHeightUnits,
        int columnWidthUnits,
        Map<Coordinate, CellDTO> activeCells,
        Map<Coordinate, List<Coordinate>> cellDependents,
        Map<Coordinate, List<Coordinate>> cellReferences,
        List<CellDTO> lastModifiedCells,
        int versionNumber
) {
}
