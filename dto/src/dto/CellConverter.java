package dto;

import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellImpl;
import engine.sheet.cell.impl.CellStyleImpl;
import engine.sheet.coordinate.Coordinate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CellConverter {

    public static CellDTO convertToDTO(Cell cell) {
        return new CellDTO(
                cell.getCoordinate(),
                cell.getOriginalValue(),
                cell.getEffectiveValue(),
                cell.getLastModifiedVersion(),
                cell.getDependsOn(),
                cell.getInfluenceOn(),
                cell.getStyle()
        );
    }
}