package dto;

import engine.sheet.coordinate.*;

import java.util.Map;

public record SheetDTO(
        String name,
        int numOfRows,
        int numOfColumns,
        int rowHeightUnits,
        int columnWidthUnits,
        Map<Coordinate, CellDTO> activeCells,
        Map<Coordinate, Coordinate> dependentCells
) {
}
