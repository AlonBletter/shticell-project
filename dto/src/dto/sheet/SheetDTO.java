package dto.sheet;

import dto.cell.CellDTO;
import dto.range.RangeDTO;
import dto.coordinate.Coordinate;

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
