package engine;

import dto.coordinate.Coordinate;
import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;
import dto.range.RangeDTO;
import dto.requestinfo.SheetInfoDTO;
import dto.sheet.SheetDTO;
import engine.permission.PermissionManager;
import engine.permission.PermissionManagerImpl;
import engine.sheet.api.SheetReadActions;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EngineImpl implements Engine {
    Map<String, SheetManager> sheetsInSystem = new ConcurrentHashMap<>(); // Sheet Name -> SheetManager
    PermissionManager permissionManager = new PermissionManagerImpl();

    @Override
    public void loadSheet(String username, InputStream fileToLoadInputStream) {
        if(fileToLoadInputStream == null) {
            throw new IllegalArgumentException("File to load cannot be null");
        }

        SheetManager sheetManager = new SheetManagerImpl();
        sheetManager.loadSystemSettingsFromFile(username, fileToLoadInputStream);
        SheetReadActions sheetReadActions = sheetManager.getSheetReadActions();
        String sheetName = sheetReadActions.getName();

        if (sheetsInSystem.keySet().stream().anyMatch(name -> name.equalsIgnoreCase(sheetName))) {
            throw new IllegalArgumentException(sheetName + " is already loaded.");
        }

        sheetsInSystem.put(sheetName, sheetManager);
        permissionManager.initializeSheetPermission(sheetName, username);
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
    public SheetDTO getSheet(String username, String sheetName) {
        permissionManager.validateReaderPermission(username, sheetName);
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

    @Override
    public SheetDTO updateCell(String username, String sheetName, Coordinate coordinate, String newValue, int sheetVersionFromUser) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateWriterPermission(username, sheetName);
        return sheet.updateCell(username, coordinate, newValue, sheetVersionFromUser);
    }

    @Override
    public SheetDTO getSheetByVersion(String username, String sheetName, int version) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateReaderPermission(username, sheetName);
        return sheet.getSheetByVersion(version);
    }

    @Override
    public void updateCellBackgroundColor(String username, String sheetName, Coordinate coordinate, String value, int sheetVersionFromUser) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateWriterPermission(username, sheetName);
        sheet.updateCellBackgroundColor(coordinate, value, sheetVersionFromUser);
    }

    @Override
    public void updateCellTextColor(String username, String sheetName, Coordinate coordinate, String value, int sheetVersionFromUser) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateWriterPermission(username, sheetName);
        sheet.updateCellTextColor(coordinate, value, sheetVersionFromUser);
    }

    @Override
    public RangeDTO addRange(String username, String sheetName, String rangeName, String coordinates, int sheetVersionFromUser) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateWriterPermission(username, sheetName);
        sheet.addRange(rangeName, coordinates, sheetVersionFromUser);
        return sheet.getRange(rangeName);
    }

    @Override
    public void deleteRange(String username, String sheetName, String rangeName, int sheetVersionFromUser) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateReaderPermission(username, sheetName);
        sheet.deleteRange(rangeName, sheetVersionFromUser);
    }

    @Override
    public SheetDTO getSortedSheet(String username, String sheetName, String range, List<String> columns) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateReaderPermission(username, sheetName);
        return sheet.getSortedSheet(range, columns);
    }

    @Override
    public SheetDTO getExpectedValue(String username, String sheetName, Coordinate coordinate, String value, int sheetVersionFromUser) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateReaderPermission(username, sheetName);
        return sheet.getExpectedValue(coordinate, value, sheetVersionFromUser);
    }

    @Override
    public SheetDTO getFilteredSheet(String username, String sheetName, String rangeToFilter, Map<String, List<String>> filterRequestValues) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateReaderPermission(username, sheetName);
        return sheet.getFilteredSheet(rangeToFilter, filterRequestValues);
    }

    @Override
    public List<Coordinate> getAxis(String username, String sheetName, String axisRange) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateReaderPermission(username, sheetName);
        return sheet.getAxis(axisRange);
    }

    @Override
    public int getLatestVersion(String username, String sheetName) {
        SheetManager sheet = findSheet(sheetName);
        permissionManager.validateReaderPermission(username, sheetName);
        return sheet.getCurrentVersionNumber();
    }
}

