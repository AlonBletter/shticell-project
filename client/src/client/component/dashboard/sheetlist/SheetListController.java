package client.component.dashboard.sheetlist;

import client.component.dashboard.DashboardController;
import client.component.dashboard.sheetlist.model.SingleSheetInformation;
import client.component.main.AppController;
import dto.SheetInfoDTO;
import engine.permission.PermissionType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
    private SimpleStringProperty currentSelectedSheetName = new SimpleStringProperty();
    private AppController mainController;
    private DashboardController dashboardController;

    @FXML
    void initialize() {
        numberOfSheets.bind(Bindings.size(sheetsTableView.getItems()));

        sheetsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dashboardController.setSelectedSheetName(newValue.getSheetName());
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
    }

    private void updateSheetsList(List<SheetInfoDTO> sheetInfoDTOList) {
        List<SingleSheetInformation> sheetInformationList = convertToSingleSheetInformationModel(sheetInfoDTOList);

        Platform.runLater(() -> {
            ObservableList<SingleSheetInformation> items = sheetsTableView.getItems();
            SingleSheetInformation selectedItem = sheetsTableView.getSelectionModel().getSelectedItem();
            items.clear();
            items.addAll(sheetInformationList);
            sheetsTableView.getSelectionModel().select(selectedItem);
        });
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
        listRefresher = new SheetListRefresher(
                this::updateSheetsList,
                numberOfSheets
        );

        timer = new Timer();
        timer.schedule(listRefresher, 0, REFRESH_RATE);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
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
