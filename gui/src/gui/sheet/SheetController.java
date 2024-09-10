package gui.sheet;

public interface SheetController {
    void markCellsButtonActionListener(boolean isMarked);
    void updateCellContent(String cellId, String content);
}
