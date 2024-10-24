package engine;

import dto.SheetDTO;
import dto.SheetInfoDTO;
import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;
import engine.sheet.coordinate.Coordinate;

import java.io.InputStream;
import java.util.List;

public interface Engine {
    void loadSheet(String username, InputStream fileToLoadInputStream);
    List<SheetInfoDTO> getSheetsInSystem(String username);
    SheetDTO getSheet(String username, String sheetName);
    List<PermissionInfoDTO> getSheetPermissionRequests(String sheetName);

    void requestPermission(String sheetName, String username, PermissionType permission);

    void handlePermissionRequest(int requestId, String sheetName, String ownerUsername, boolean ownerDecision);

    SheetDTO updateCell(String username, String sheetName, Coordinate coordinate, String newValue);

    SheetDTO getSheetByVersion(String username, String sheetName, int version);
}
