package engine;

import dto.SheetDTO;
import dto.SheetInfoDTO;

import java.io.InputStream;
import java.util.List;

public interface Engine {
    void loadSheet(String username, InputStream fileToLoadInputStream);
    List<SheetInfoDTO> getSheetsInSystem();
    SheetDTO getSheet(String sheetName);
}
