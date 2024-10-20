package client.component.dashboard.permission.model;

import dto.permission.PermissionType;
import dto.permission.RequestStatus;

public class PermissionRequest {
    private int requestId;
    private String username;
    private PermissionType type;
    private RequestStatus status;

    public PermissionRequest(int requestId, String username, PermissionType type, RequestStatus status) {
        this.requestId = requestId;
        this.username = username;
        this.type = type;
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
