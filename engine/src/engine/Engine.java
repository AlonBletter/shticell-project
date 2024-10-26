package engine;

import dto.RangeDTO;
import dto.SheetDTO;
import dto.SheetInfoDTO;
import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;
import engine.sheet.coordinate.Coordinate;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {
    void loadSheet(String username, InputStream fileToLoadInputStream);
    List<SheetInfoDTO> getSheetsInSystem(String username);
    SheetDTO getSheet(String username, String sheetName);
    List<PermissionInfoDTO> getSheetPermissionRequests(String sheetName);

    void requestPermission(String sheetName, String username, PermissionType permission);

    void handlePermissionRequest(int requestId, String sheetName, String ownerUsername, boolean ownerDecision);

    SheetDTO updateCell(String username, String sheetName, Coordinate coordinate, String newValue);

    SheetDTO getSheetByVersion(String username, String sheetName, int version);

    void updateCellBackgroundColor(String username, String sheetName, Coordinate coordinate, String value);

    void updateCellTextColor(String username, String sheetName, Coordinate coordinate, String value);

    RangeDTO addRange(String username, String sheetName, String rangeName, String coordinates);

    void deleteRange(String username, String sheetName, String rangeName);

    SheetDTO getSortedSheet(String username, String sheetName, String range, List<String> columns);

    SheetDTO getExpectedValue(String username, String sheetName, Coordinate coordinate, String value);

    SheetDTO getFilteredSheet(String username, String sheetName, String rangeToFilter, Map<String, List<String>> filterRequestValues);

    List<Coordinate> getAxis(String username, String sheetName, String axisRange);
}
