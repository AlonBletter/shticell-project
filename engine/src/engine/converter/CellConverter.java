package engine.converter;

import dto.cell.CellDTO;
import dto.cell.CellStyleDTO;
import engine.sheet.cell.api.CellReadActions;

public class CellConverter {

    public static CellDTO convertToDTO(CellReadActions cell) {
        return new CellDTO(
                cell.getCoordinate(),
                cell.getOriginalValue(),
                cell.getEffectiveValue(),
                cell.getLastModifiedVersion(),
                cell.getLastModifiedBy(),
                cell.getDependsOn(),
                cell.getInfluenceOn(),
                new CellStyleDTO(cell.getStyle()),
                cell.isContainFunction()
        );
    }
}