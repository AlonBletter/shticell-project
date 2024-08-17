package dto;

public record SheetDTO(
        String name,
        int width,
        int height,
        CellDTO[][] cells
) {
}
