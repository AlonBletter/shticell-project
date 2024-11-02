package engine.permission;

import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;
import dto.permission.RequestStatus;
import engine.permission.request.PermissionRequest;
import engine.permission.request.PermissionRequestImpl;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PermissionManagerImpl implements PermissionManager {
    private final Map<String, Map<String, PermissionType>> sheetPermissions; // Sheet name -> (User -> PermissionType)
    private final Map<String, Map<Integer, PermissionRequest>> permissionRequests; // Sheet name -> (Request ID -> (User, PermissionType, Status))
    private final Map<String, String> owners; // Sheet name -> User
    private static int requestID = 0;
    private final ReentrantReadWriteLock sheetPermissionsLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock permissionRequestsLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock ownersLock = new ReentrantReadWriteLock();

    public PermissionManagerImpl() {
        sheetPermissions = new HashMap<>();
        permissionRequests = new HashMap<>();
        owners = new HashMap<>();
    }

    private void assignPermission(String sheetName, String username, PermissionType permission) {
        sheetPermissionsLock.writeLock().lock();
        try {
            sheetPermissions.get(sheetName).put(username, permission);
        } finally {
            sheetPermissionsLock.writeLock().unlock();
        }
    }

    @Override
    public void initializeSheetPermission(String sheetName, String username) {
        sheetPermissionsLock.writeLock().lock();
        try {
            sheetPermissions.put(sheetName, new HashMap<>());

            assignPermission(sheetName, username, PermissionType.OWNER);

            ownersLock.writeLock().lock();
            try {
                owners.put(sheetName, username);
            } finally {
                ownersLock.writeLock().unlock();
            }
        } finally {
            sheetPermissionsLock.writeLock().unlock();
        }
    }

    @Override
    public String getOwner(String sheetName) {
        ownersLock.readLock().lock();
        try {
            return owners.get(sheetName);
        } finally {
            ownersLock.readLock().unlock();
        }
    }

    @Override
    public void handleRequest(int requestId, String sheetName, String decisionSentBy, boolean ownerDecision) {
        validateSheet(sheetName);
        if(!decisionSentBy.equals(getOwner(sheetName))) {
            throw new IllegalArgumentException(decisionSentBy + " isn't the owner of the sheet " + sheetName);
        } else if (requestId == 0) {
            throw new IllegalArgumentException("The owner cannot modify his own permission.");
        }

        permissionRequestsLock.writeLock().lock();
        try {
            PermissionRequest request = permissionRequests.get(sheetName).get(requestId);

            if (request != null) {
                String username = request.getUsername();

                if (request.getRequestStatus() != RequestStatus.PENDING) {
                    throw new IllegalArgumentException("Request has already been decided.");
                }

                if (ownerDecision) {
                    assignPermission(sheetName, username, request.getPermissionType());
                    request.setRequestStatus(RequestStatus.APPROVED);
                } else {
                    request.setRequestStatus(RequestStatus.REJECTED);
                }
            } else {
                throw new IllegalArgumentException("Permission request not found for requestId: " + requestId);
            }
        } finally {
            permissionRequestsLock.writeLock().unlock();
        }
    }

    @Override
    public void requestPermission(String sheetName, String username, PermissionType requestedPermission) {
        validateSheet(sheetName);
        if(username.equals(getOwner(sheetName))) {
            throw new IllegalArgumentException("The owner cannot request permission");
        }

        permissionRequestsLock.writeLock().lock();
        try {
            requestID++;
            permissionRequests.computeIfAbsent(sheetName, k -> new HashMap<>())
                    .put(requestID, new PermissionRequestImpl(username, requestedPermission, RequestStatus.PENDING));
        } finally {
            permissionRequestsLock.writeLock().unlock();
        }
    }

    @Override
    public void validateWriterPermission(String username, String sheetName) {
        validateSheet(sheetName);

        PermissionType permission;
        sheetPermissionsLock.readLock().lock();
        try {
            permission = sheetPermissions.get(sheetName).get(username);
        } finally {
            sheetPermissionsLock.readLock().unlock();
        }

        if(permission != PermissionType.OWNER && permission != PermissionType.WRITER) {
            throw new IllegalArgumentException("User [" + username + "] does not have writer permission for sheet [" + sheetName + "].");
        }
    }

    @Override
    public void validateReaderPermission(String username, String sheetName) {
        validateSheet(sheetName);
        PermissionType permission;
        sheetPermissionsLock.readLock().lock();
        try {
            permission = sheetPermissions.get(sheetName).get(username);
        } finally {
            sheetPermissionsLock.readLock().unlock();
        }

        if(permission == PermissionType.NONE) {
            throw new IllegalArgumentException("User [" + username + "] does not have permission for sheet [" + sheetName + "].");
        }
    }

    @Override
    public PermissionType getUserPermission(String sheetName, String username) {
        validateSheet(sheetName);
        sheetPermissionsLock.readLock().lock();
        try {
            return sheetPermissions.get(sheetName).getOrDefault(username, PermissionType.NONE);
        } finally {
            sheetPermissionsLock.readLock().unlock();
        }
    }

    private void validateSheet(String sheetName) {
        sheetPermissionsLock.readLock().lock();
        try {
            if (!sheetPermissions.containsKey(sheetName)) {
                throw new IllegalArgumentException("Sheet " + sheetName + " does not exist");
            }
        } finally {
            sheetPermissionsLock.readLock().unlock();
        }
    }

    @Override
    public List<PermissionInfoDTO> getRequests(String sheetName) {
        List<PermissionInfoDTO> requestDTOs = new ArrayList<>();
        String sheetOwner = getOwner(sheetName);

        PermissionInfoDTO ownerInfo = new PermissionInfoDTO(
                0,
                sheetOwner,
                PermissionType.OWNER,
                null
        );

        requestDTOs.add(ownerInfo);

        permissionRequestsLock.readLock().lock();
        try{
            Map<Integer, PermissionRequest> requests = permissionRequests.get(sheetName);

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

        } finally {
            permissionRequestsLock.readLock().unlock();
        }

        return new ArrayList<>(requestDTOs);
    }
}
