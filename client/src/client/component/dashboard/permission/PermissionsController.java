package client.component.dashboard.permission;

import client.component.dashboard.DashboardController;
import client.component.dashboard.permission.model.PermissionRequest;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import dto.permission.PermissionInfoDTO;
import dto.permission.PermissionType;
import dto.permission.RequestStatus;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static client.util.Constants.GSON_INSTANCE;

public class PermissionsController {

    @FXML private VBox permissionsComponent;
    @FXML private Label permissionsListView;
    @FXML private TableView<PermissionRequest> permissionsTableView;
    @FXML private TableColumn<PermissionRequest, String> usernameColumn;
    @FXML private TableColumn<PermissionRequest, PermissionType> permissionTypeColumn;
    @FXML private TableColumn<PermissionRequest, RequestStatus> statusColumn;

    private DashboardController dashboardController;

    @FXML
    void initialize() {
        usernameColumn.setCellValueFactory(permissionData ->
                new SimpleStringProperty(permissionData.getValue().getUsername()));

        permissionTypeColumn.setCellValueFactory(permissionData ->
                new SimpleObjectProperty<>(permissionData.getValue().getType()));

        statusColumn.setCellValueFactory(permissionData ->
                new SimpleObjectProperty<>(permissionData.getValue().getStatus()));

        permissionsTableView.setPlaceholder(new Label("No permissions requests"));

        permissionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                dashboardController.requestIDProperty().set(newValue.getRequestId());
            }
        });

        permissionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            dashboardController.isPermissionRequestSelectedProperty().set(newValue != null);
        });
    }

    public void     setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;

        dashboardController.selectedSheetNameProperty().addListener((observable, oldValue, newValue) -> {
            handlePermissionsRequest(newValue);
        });
    }

    private void handlePermissionsRequest(String sheetName) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_PERMISSION_REQUESTS)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        Consumer<String> responseHandler = (response) -> {
            if (response != null) {
                PermissionInfoDTO[] permissionInfoDTOS = GSON_INSTANCE.fromJson(response, PermissionInfoDTO[].class);

                if (permissionInfoDTOS != null) {
                    List<PermissionRequest> permissionRequests = convertToPermissionRequest(Arrays.asList(permissionInfoDTOS));
                    updatePermissionsTable(permissionRequests);
                }
            }
        };

        HttpClientUtil.runReqAsyncWithJson(finalUrl, HttpMethod.GET, null, responseHandler);
    }

    private void updatePermissionsTable(List<PermissionRequest> permissionRequests) {
        ObservableList<PermissionRequest> items = permissionsTableView.getItems();
        PermissionRequest selectedItem = permissionsTableView.getSelectionModel().getSelectedItem();

        Platform.runLater(() -> {
            items.clear();
            items.addAll(permissionRequests);
            permissionsTableView.getSelectionModel().select(selectedItem);
        });
    }

    private List<PermissionRequest> convertToPermissionRequest(List<PermissionInfoDTO> permissionInfoDTOS) {
        return permissionInfoDTOS.stream()
                .map(dto -> new PermissionRequest(
                        dto.requestId(),
                        dto.requestByUsername(),
                        dto.permissionType(),
                        dto.requestStatus()))
                .toList();
    }

    public void refreshView() {
        handlePermissionsRequest(dashboardController.selectedSheetNameProperty().getValue());
    }
}
