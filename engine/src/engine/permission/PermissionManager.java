package engine.permission;

import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;

import java.util.List;

public interface PermissionManager {
    void assignPermission(String sheetName, String username, PermissionType permission);

    void initializeSheetPermission(String sheetName, String username);

    String getOwner(String sheetName);

    void validateWriterPermission(String sheetName, String username);

    void validateReaderPermission(String sheetName, String username);

    PermissionType getUserPermission(String sheetName, String username);

    void requestPermission(String sheetName, String username, PermissionType requestedPermission);

    void handleRequest(int requestId, String sheetName, String decisionSentBy, boolean ownerDecision);

    List<PermissionInfoDTO> getRequests(String sheetName);
}
