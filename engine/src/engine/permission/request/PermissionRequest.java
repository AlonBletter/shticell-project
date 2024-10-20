package engine.permission.request;

import dto.permission.PermissionType;
import dto.permission.RequestStatus;

public interface PermissionRequest {
    String getUsername();

    void setUsername(String username);

    PermissionType getPermissionType();

    void setPermissionType(PermissionType permissionType);

    RequestStatus getRequestStatus();

    void setRequestStatus(RequestStatus requestStatus);
}
