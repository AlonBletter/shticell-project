package engine.permission;

import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;
import dto.permission.RequestStatus;
import engine.permission.request.PermissionRequest;
import engine.permission.request.PermissionRequestImpl;

import java.util.*;

public class PermissionManagerImpl implements PermissionManager {
    private final Map<String, Map<String, PermissionType>> sheetPermissions; // Sheet name -> (User -> PermissionType)
    private final Map<String, Map<Integer, PermissionRequest>> permissionRequests; // Sheet name -> (Request ID -> (User, PermissionType, Status))
    private final Map<String, String> owners; // Sheet name -> User
    private static int requestID = 0;

    public PermissionManagerImpl() {
        sheetPermissions = new HashMap<>();
        permissionRequests = new HashMap<>();
        owners = new HashMap<>();
    }

    @Override
    public void assignPermission(String sheetName, String username, PermissionType permission) {
        sheetPermissions.computeIfAbsent(sheetName, k -> new HashMap<>()).put(username, permission);

        if(permission == PermissionType.OWNER) {
            owners.put(sheetName, username);
        }
    }

    @Override
    public String getOwner(String sheetName) {
        return owners.get(sheetName);
    }

    @Override
    public boolean hasPermission(String sheetName, String username, PermissionType requiredPermission) {
        return sheetPermissions.containsKey(sheetName) &&
                sheetPermissions.get(sheetName).getOrDefault(username, PermissionType.NONE)
                        .compareTo(requiredPermission) >= 0;
    }

    @Override
    public PermissionType getUserPermission(String sheetName, String username) {
        return sheetPermissions.get(sheetName).getOrDefault(username, PermissionType.NONE);
    }

    @Override
    public void requestPermission(String sheetName, String username, PermissionType requestedPermission) {
        validateSheet(sheetName);
        if(username.equals(owners.get(sheetName))) {
            throw new IllegalArgumentException("The owner cannot request permission");
        }

        requestID++;
        permissionRequests.computeIfAbsent(sheetName, k -> new HashMap<>())
                .put(requestID, new PermissionRequestImpl(username, requestedPermission, RequestStatus.PENDING));
    }

    private void validateSheet(String sheetName) {
        if (!sheetPermissions.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet " + sheetName + " does not exist");
        }
    }

    @Override
    public void handleRequest(int requestId, String sheetName, String decisionSentBy, boolean ownerDecision) {
        validateSheet(sheetName);
        if(!decisionSentBy.equals(owners.get(sheetName))) {
            throw new IllegalArgumentException(decisionSentBy + " isn't the owner of the sheet " + sheetName);
        } else if (requestId == 0) {
            throw new IllegalArgumentException("The owner cannot modify his own permission.");
        }

        PermissionRequest request = permissionRequests.get(sheetName).get(requestId);

        if (request != null) {
            String username = request.getUsername();

            if (request.getRequestStatus() != RequestStatus.PENDING) {
                throw new IllegalArgumentException("Request has already been decided.");
            }

            if (ownerDecision) {
                assignPermission(sheetName, username, request.getPermissionType());
                request.setRequestStatus(RequestStatus.APPROVED);
            }
            else {
                request.setRequestStatus(RequestStatus.REJECTED);
            }
        } else {
            System.err.println("Permission request not found for requestId: " + requestId);
        }
    }

    @Override
    public List<PermissionInfoDTO> getRequests(String sheetName) {
        Map<Integer, PermissionRequest> requests = permissionRequests.get(sheetName);
        List<PermissionInfoDTO> requestDTOs = new LinkedList<>();
        PermissionInfoDTO ownerInfo = new PermissionInfoDTO(
                0,
                owners.get(sheetName),
                PermissionType.OWNER,
                null
        );

        requestDTOs.add(ownerInfo);

        if (requests != null) {
            for (Map.Entry<Integer, PermissionRequest> entry : requests.entrySet()) {
                PermissionRequest request = entry.getValue();

                PermissionInfoDTO dto = new PermissionInfoDTO(
                        entry.getKey(),
                        request.getUsername(),
                        request.getPermissionType(),
                        request.getRequestStatus()
                );

                requestDTOs.add(dto);
            }
        }

        return new ArrayList<>(requestDTOs);
    }
}
