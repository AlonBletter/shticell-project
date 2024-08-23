package dto;

import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellImpl;

public class CellConverter {

    public static CellDTO convertToDTO(Cell cell) {
        return new CellDTO(
                cell.getOriginalValue(),
                cell.getEffectiveValue(),
                cell.getLastModifiedVersion()
        );
    }

    public static Cell convertToEntity(CellDTO cellDTO) {
        return new CellImpl(
                cellDTO.originalValue(),
                cellDTO.effectiveValue(),
                cellDTO.lastModifiedVersion()
        );
    }
}