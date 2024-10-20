package dto.permission;

public record PermissionInfoDTO(
        int requestId,
        String requestByUsername,
        PermissionType permissionType,
        RequestStatus requestStatus
) {
}
