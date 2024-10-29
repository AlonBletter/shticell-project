package dto.sheet;

import dto.sheet.cell.CellDTO;
import dto.sheet.range.RangeDTO;
import engine.sheet.coordinate.Coordinate;

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
        int versionNumber,
        List<RangeDTO> ranges
) {
}
