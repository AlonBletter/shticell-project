package engine;

import dto.SheetDTO;
import dto.SheetInfoDTO;

import java.io.InputStream;
import java.util.*;

public class EngineImpl implements Engine {
    Map<String, SheetManager> sheetsInSystem = new HashMap<>();

    @Override
    public void loadSheet(String username, InputStream fileToLoadInputStream) {
        if(fileToLoadInputStream == null) {
            throw new IllegalArgumentException("File to load cannot be null");
        }

        SheetManager sheetManager = new SheetManagerImpl(username);
        sheetManager.loadSystemSettingsFromFile(fileToLoadInputStream);
        SheetInfoDTO sheetInfo = sheetManager.getSheetInfo();

        if (sheetsInSystem.keySet().stream().anyMatch(name -> name.equalsIgnoreCase(sheetInfo.name()))) {
            throw new IllegalArgumentException(sheetInfo.name() + " is already loaded.");
        }

        sheetsInSystem.put(sheetInfo.name(), sheetManager);
    }

    @Override
    public List<SheetInfoDTO> getSheetsInSystem() {
        List<SheetInfoDTO> sheetInfoDTOSet = new LinkedList<>();

        for(SheetManager sheetManager : sheetsInSystem.values()) {
            sheetInfoDTOSet.add(sheetManager.getSheetInfo());
        }

        return sheetInfoDTOSet;
    }

    @Override
    public SheetDTO getSheet(String sheetName) {
        return findSheet(sheetName).getSpreadsheet();
    }

    private SheetManager findSheet(String sheetName) {
        SheetManager sheetManager = sheetsInSystem.get(sheetName);

        if (sheetManager == null) {
            throw new IllegalArgumentException("Sheet " + sheetName + " not found");
        }

        return sheetManager;
    }
}
