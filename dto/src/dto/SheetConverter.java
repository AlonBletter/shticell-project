package dto;

import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.impl.SheetImpl;

import java.util.*;

public class SheetConverter {
    public static SheetDTO convertToDTO(Sheet sheet) {
        return new SheetDTO(
                sheet.getName(),
                sheet.getNumberOfRows(),
                sheet.getNumberOfColumns(),
                sheet.getRowHeightUnits(),
                sheet.getColumnWidthUnits(),
                convertActiveCellsToDTO(sheet.getActiveCells()),
                sheet.getCellDependents(),
                sheet.getCellReferences(),
                convertVersionsToDTO(sheet.getVersions()),
                convertLastModifiedCellsToDTO(sheet.getLastModifiedCells())
        );
    }

//    public static Sheet convertToEntity(SheetDTO sheetDTO) {
//        return new SheetImpl(
//                sheetDTO.name(),
//                sheetDTO.numOfRows(),
//                sheetDTO.numOfColumns(),
//                sheetDTO.rowHeightUnits(),
//                sheetDTO.columnWidthUnits(),
//                convertActiveCellsToEntity(sheetDTO.activeCells()),
//                sheetDTO.cellDependents(),
//                sheetDTO.cellReferences()
//        );
//    }

    private static List<CellDTO> convertLastModifiedCellsToDTO(List<Cell> lastModifiedCells) {
        List<CellDTO> cellDTOs = new LinkedList<>();

        for (Cell cell : lastModifiedCells) {
            CellDTO cellDTO = CellConverter.convertToDTO(cell);
            cellDTOs.add(cellDTO);
        }

        return cellDTOs;
    }

    private static Map<Integer, SheetDTO> convertVersionsToDTO(Map<Integer, Sheet> versions) {
        Map<Integer, SheetDTO> versionsDTO = new HashMap<>();

        for (Map.Entry<Integer, Sheet> entry : versions.entrySet()) {
            int versionNum = entry.getKey();
            Sheet sheet = entry.getValue();
            SheetDTO sheetDTO = convertToDTO(sheet);

            versionsDTO.put(versionNum, sheetDTO);
        }

        return versionsDTO;
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
}