package dto;

import engine.Cell;

public record SpreadsheetDTO(
        String name,
        int width,
        int height,
        Cell[][] cells
) {
}
