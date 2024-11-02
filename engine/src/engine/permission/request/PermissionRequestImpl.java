package engine.permission.request;

import dto.permission.PermissionType;
import dto.permission.RequestStatus;

public class PermissionRequestImpl implements PermissionRequest {
    private String username;
    private PermissionType permissionType;
    private RequestStatus requestStatus;

    public PermissionRequestImpl(String username, PermissionType permissionType, RequestStatus requestStatus) {
        this.username = username;
        this.permissionType = permissionType;
        this.requestStatus = requestStatus;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public PermissionType getPermissionType() {
        return permissionType;
    }

    @Override
    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
