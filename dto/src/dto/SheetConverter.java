package dto;

import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

public class SheetConverter {
    public static SheetDTO convertToDTO(Sheet sheet) {
        return new SheetDTO(
                sheet.getName(),
                sheet.getNumberOfRows(),
                sheet.getNumberOfColumns(),
                sheet.getRowHeightUnits(),
                sheet.getColumnWidthUnits(),
                convertActiveCellsToDTO(sheet.getActiveCells()),
                copyRelations(sheet.getCellInfluenceOn()),
                copyRelations(sheet.getCellDependsOn()),
                convertLastModifiedCellsToDTO(sheet.getLastModifiedCells()),
                sheet.getVersionNumber()
        );
    }

    private static List<CellDTO> convertLastModifiedCellsToDTO(List<Cell> lastModifiedCells) {
        List<CellDTO> cellDTOs = new LinkedList<>();

        for (Cell cell : lastModifiedCells) {
            CellDTO cellDTO = CellConverter.convertToDTO(cell);
            cellDTOs.add(cellDTO);
        }

        return cellDTOs;
    }

    private static Map<Coordinate, CellDTO> convertActiveCellsToDTO(Map<Coordinate, Cell> activeCells) {
        Map<Coordinate, CellDTO> activeCellsDTO = new HashMap<>();

        for (Map.Entry<Coordinate, Cell> entry : activeCells.entrySet()) {
            Coordinate coordinate = entry.getKey();
            Cell cell = entry.getValue();
            CellDTO cellDTO = CellConverter.convertToDTO(cell);

            activeCellsDTO.put(coordinate, cellDTO);
        }

        return activeCellsDTO;
    }

    public static Map<Coordinate, List<Coordinate>> copyRelations(Map<Coordinate, List<Coordinate>> original) {
        Map<Coordinate, List<Coordinate>> copy = new HashMap<>();

        for (Map.Entry<Coordinate, List<Coordinate>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new LinkedList<>(entry.getValue()));
        }

        return copy;
    }
}