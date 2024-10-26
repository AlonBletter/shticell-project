package dto;

import dto.permission.PermissionType;

public record SheetInfoDTO(String uploadedByUser, String name, int numOfRows, int numOfColumns,
                           PermissionType currentUserPermissionForSheet) {

    public static SheetInfoDTO getSheetInfoDTO(
            String owner,
            String sheetName,
            int numOfRows,
            int numOfColumns,
            PermissionType currentUserPermissionForSheet) {

        return new SheetInfoDTO(
                owner,
                sheetName,
                numOfRows,
                numOfColumns,
                currentUserPermissionForSheet
        );
    }

    @Override
    public String toString() {
        return "SheetInfoDTO[" +
                "uploadedByUser=" + uploadedByUser + ", " +
                "name=" + name + ", " +
                "numOfRows=" + numOfRows + ", " +
                "numOfColumns=" + numOfColumns + ", " +
                "currentUserPermissionForSheet=" + currentUserPermissionForSheet + ']';
    }
}
