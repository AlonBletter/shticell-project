package engine;

import dto.SheetDTO;
import dto.SheetInfoDTO;
import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;
import engine.permission.PermissionManager;
import engine.permission.PermissionManagerImpl;
import engine.sheet.api.SheetReadActions;

import java.io.InputStream;
import java.util.*;

public class EngineImpl implements Engine {
    Map<String, SheetManager> sheetsInSystem = new HashMap<>(); // Sheet Name -> SheetManager
    PermissionManager permissionManager = new PermissionManagerImpl();

    @Override
    public void loadSheet(String username, InputStream fileToLoadInputStream) {
        if(fileToLoadInputStream == null) {
            throw new IllegalArgumentException("File to load cannot be null");
        }

        SheetManager sheetManager = new SheetManagerImpl();
        sheetManager.loadSystemSettingsFromFile(fileToLoadInputStream);
        SheetReadActions sheetReadActions = sheetManager.getSheetReadActions();
        String sheetName = sheetReadActions.getName();

        if (sheetsInSystem.keySet().stream().anyMatch(name -> name.equalsIgnoreCase(sheetName))) {
            throw new IllegalArgumentException(sheetName + " is already loaded.");
        }

        sheetsInSystem.put(sheetName, sheetManager);
        permissionManager.assignPermission(sheetName, username, PermissionType.OWNER);
    }

    @Override
    public List<SheetInfoDTO> getSheetsInSystem(String username) {
        List<SheetInfoDTO> sheetInfoDTOSet = new LinkedList<>();

        for(SheetManager sheetManager : sheetsInSystem.values()) {
            SheetReadActions sheetReadActions = sheetManager.getSheetReadActions();
            String sheetName = sheetReadActions.getName();
            String owner = permissionManager.getOwner(sheetName);
            int numOfRows = sheetReadActions.getNumberOfRows();
            int numOfColumns = sheetReadActions.getNumberOfColumns();
            PermissionType permissionType = permissionManager.getUserPermission(sheetName, username);

            sheetInfoDTOSet.add(SheetInfoDTO
                    .getSheetInfoDTO(owner, sheetName, numOfRows, numOfColumns, permissionType));
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

    @Override
    public List<PermissionInfoDTO> getSheetPermissionRequests(String sheetName) {
        return permissionManager.getRequests(sheetName);
    }

    @Override
    public void requestPermission(String sheetName, String username, PermissionType permission) {
        permissionManager.requestPermission(sheetName, username, permission);
    }

    @Override
    public void handlePermissionRequest(int requestId, String sheetName, String ownerUsername, boolean ownerDecision) {
        permissionManager.handleRequest(requestId, sheetName, ownerUsername, ownerDecision);
    }


}
