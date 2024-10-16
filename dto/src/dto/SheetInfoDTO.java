package dto;

import engine.permission.PermissionType;
import engine.sheet.api.Sheet;

import java.util.Objects;

public record SheetInfoDTO(String uploadedByUser, String name, int numOfRows, int numOfColumns,
                           PermissionType currentUserPermissionForSheet) {

    public static SheetInfoDTO getSheetInfoDTO(String owner, Sheet sheet) {

        return new SheetInfoDTO(
                owner,
                sheet.getName(),
                sheet.getNumberOfRows(),
                sheet.getNumberOfColumns(),
                null //TODO implement!
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
