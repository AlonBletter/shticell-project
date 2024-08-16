package dto;

import engine.sheet.cell.api.Cell;

public record SpreadsheetDTO(
        String name,
        int width,
        int height,
        Cell[][] cells
) {
}
