package client.component.dashboard.sheetlist.model;

import dto.permission.PermissionType;
import javafx.beans.property.SimpleStringProperty;

public class SingleSheetInformation {
    private String owner;
    private SimpleStringProperty sheetName;
    private int numOfRows;
    private int numOfColumns;
    private PermissionType permissionType;

    public SingleSheetInformation(String owner, String sheetName, int numOfRows, int numOfColumns, PermissionType permissionType) {
        this.owner = owner;
        this.sheetName = new SimpleStringProperty(sheetName);
        this.numOfRows = numOfRows;
        this.numOfColumns = numOfColumns;
        this.permissionType = permissionType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSheetName() {
        return sheetName.get();
    }

    public SimpleStringProperty sheetNameProperty() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName.set(sheetName);
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public int getNumOfColumns() {
        return numOfColumns;
    }

    public void setNumOfColumns(int numOfColumns) {
        this.numOfColumns = numOfColumns;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
