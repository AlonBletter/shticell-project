package dto.converter;

import dto.CellDTO;
import dto.RangeDTO;
import dto.SheetDTO;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
                copyRelations(sheet.getCellInfluenceOn()),
                copyRelations(sheet.getCellDependsOn()),
                convertLastModifiedCellsToDTO(sheet.getLastModifiedCells()),
                sheet.getVersionNumber(),
                copyRanges(sheet.getRanges())
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

    private static List<RangeDTO> copyRanges(List<Range> ranges) {
        List<RangeDTO> rangeDTOs = new LinkedList<>();

        for(Range range : ranges) {
            RangeDTO rangeDTO = RangeDTO.convertToRangeDTO(range);
            rangeDTOs.add(rangeDTO);
        }

        return rangeDTOs;
    }
}