package dto;

import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.impl.SheetImpl;

import java.util.HashMap;
import java.util.Map;

public class SheetConverter {
    public static SheetDTO convertToDTO(Sheet sheet) {
        return new SheetDTO(
                sheet.getName(),
                sheet.getNumberOfRows(),
                sheet.getNumberOfColumns(),
                sheet.getRowHeightUnits(),
                sheet.getColumnWidthUnits(),
                convertActiveCellsToDTO(sheet.getActiveCells()),
                sheet.getDependentCells()
        );
    }

    public static Sheet convertToEntity(SheetDTO sheetDTO) {
        return new SheetImpl(
                sheetDTO.name(),
                sheetDTO.numOfRows(),
                sheetDTO.numOfColumns(),
                sheetDTO.rowHeightUnits(),
                sheetDTO.columnWidthUnits(),
                convertActiveCellsToEntity(sheetDTO.activeCells()),
                sheetDTO.dependentCells()
        );
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

    private static Map<Coordinate, Cell> convertActiveCellsToEntity(Map<Coordinate, CellDTO> activeCells) {
        Map<Coordinate, Cell> activeCellsEntity = new HashMap<>();

        for (Map.Entry<Coordinate, CellDTO> entry : activeCells.entrySet()) {
            Coordinate coordinate = entry.getKey();
            CellDTO cellDTO = entry.getValue();
            Cell cell = CellConverter.convertToEntity(cellDTO);

            activeCellsEntity.put(coordinate, cell);
        }

        return activeCellsEntity;
    }

//    private static Map<Coordinate, Coordinate> convertDependantCellsToDTO(Map<Coordinate, Coordinate> activeCells) {
//        Map<Coordinate, Coordinate> dependantCellsDTO = new HashMap<>();
//
//        for (Map.Entry<Coordinate, Coordinate> entry : activeCells.entrySet()) {
//            Coordinate source = entry.getKey();
//            Coordinate destination = entry.getValue();
//            dependantCellsDTO.put(source, destination);
//        }
//
//        return dependantCellsDTO;
//    }
}