package dto;

import engine.sheet.cell.api.CellStyle;

public record CellStyleDTO(
        String backgroundColor,
        String textColor
) {
    public CellStyleDTO(CellStyle style) {
        this(style.getBackgroundColor(), style.getTextColor());
    }
}
