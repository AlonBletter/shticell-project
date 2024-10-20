package engine.permission;

import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;

import java.util.List;

public interface PermissionManager {
    void assignPermission(String sheetName, String username, PermissionType permission);

    String getOwner(String sheetName);

    boolean hasPermission(String sheetName, String username, PermissionType requiredPermission);

    PermissionType getUserPermission(String sheetName, String username);

    void requestPermission(String sheetName, String username, PermissionType requestedPermission);

    void handleRequest(int requestId, String sheetName, String decisionSentBy, boolean ownerDecision);

    List<PermissionInfoDTO> getRequests(String sheetName);
}
