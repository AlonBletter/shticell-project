package client.component.dashboard.sheetlist;

import client.component.dashboard.DashboardController;
import client.component.dashboard.sheetlist.model.SingleSheetInformation;
import client.component.main.AppController;
import dto.info.SheetInfoDTO;
import dto.permission.PermissionType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static client.util.Constants.REFRESH_RATE;

public class SheetListController implements Closeable {

    private Timer timer;
    private TimerTask listRefresher;
    @FXML private TableView<SingleSheetInformation> sheetsTableView;
    @FXML private TableColumn<SingleSheetInformation, String> userColumn;
    @FXML private TableColumn<SingleSheetInformation, String> sheetNameColumn;
    @FXML private TableColumn<SingleSheetInformation, String> dimensionsColumn;
    @FXML private TableColumn<SingleSheetInformation, PermissionType> permissionColumn;
    private SimpleIntegerProperty numberOfSheets = new SimpleIntegerProperty();
    private DashboardController dashboardController;

    @FXML
    void initialize() {
        numberOfSheets.bind(Bindings.size(sheetsTableView.getItems()));

        sheetsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dashboardController.setSelectedSheetName(newValue.getSheetName());
                dashboardController.isSelectedSheetProperty().set(true);
                dashboardController.setIsUserOwnerOfSelectedSheet(newValue.getPermissionType() == PermissionType.OWNER);
                dashboardController.setIsUserHasNoPermissions(newValue.getPermissionType() == PermissionType.NONE);
                dashboardController.setIsUserHasReaderPermission(newValue.getPermissionType() == PermissionType.READER);
            } else {
                dashboardController.isSelectedSheetProperty().set(false);
            }
        });

        userColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOwner()));

        sheetNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSheetName()));

        dimensionsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNumOfRows() + " x " + cellData.getValue().getNumOfColumns()));

        permissionColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getPermissionType()));

        sheetsTableView.setPlaceholder(new Label("No sheets available"));
    }

    private void updateSheetsList(List<SheetInfoDTO> sheetInfoDTOList) {
        if(sheetInfoDTOList.size() == sheetsTableView.getItems().size() && !wasAPermissionChanged(sheetInfoDTOList)) {
            return;
        }

        List<SingleSheetInformation> sheetInformationList = convertToSingleSheetInformationModel(sheetInfoDTOList);

        Platform.runLater(() -> {
            ObservableList<SingleSheetInformation> items = sheetsTableView.getItems();
            SingleSheetInformation selectedItem = sheetsTableView.getSelectionModel().getSelectedItem();
            items.clear();
            items.addAll(sheetInformationList);
            sheetsTableView.getSelectionModel().select(selectedItem);
        });
    }

    private boolean wasAPermissionChanged(List<SheetInfoDTO> sheetInfoDTOList) {
        ObservableList<SingleSheetInformation> items = sheetsTableView.getItems();

        for (SheetInfoDTO sheetInfoDTO : sheetInfoDTOList) {
            PermissionType currentUserPermission = sheetInfoDTO.currentUserPermissionForSheet();

            SingleSheetInformation correspondingSheet = items.stream()
                    .filter(item -> item.getSheetName().equals(sheetInfoDTO.name()))
                    .findFirst()
                    .orElse(null);

            if (correspondingSheet != null) {
                PermissionType itemPermission = correspondingSheet.getPermissionType();

                if (!currentUserPermission.equals(itemPermission)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static List<SingleSheetInformation> convertToSingleSheetInformationModel(List<SheetInfoDTO> sheetInfoDTOList) {
        return sheetInfoDTOList.stream()
                .map(dto -> new SingleSheetInformation(
                        dto.uploadedByUser(),
                        dto.name(),
                        dto.numOfRows(),
                        dto.numOfColumns(),
                        dto.currentUserPermissionForSheet()))
                .toList();
    }

    public void startListRefresher() {
        listRefresher = new SheetListRefresher(this::updateSheetsList);

        timer = new Timer();
        timer.schedule(listRefresher, 0, REFRESH_RATE);
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @Override
    public void close() {
        sheetsTableView.getItems().clear();
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }
}
