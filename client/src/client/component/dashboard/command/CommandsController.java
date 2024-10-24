package client.component.dashboard.command;

import client.component.dashboard.DashboardController;
import client.component.dashboard.command.dialog.PermissionRequestController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.SheetDTO;
import dto.permission.PermissionType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import static client.util.Constants.ADAPTED_GSON;

public class CommandsController {

    @FXML private VBox commandsComponent;
    @FXML private Button requestPermissionButton;
    @FXML private Button viewSheetButton;
    @FXML private Button refreshPermissionRequestsButton;
    @FXML private Button rejectButton;
    @FXML private Button approveButton;

    private DashboardController dashboardController;

    @FXML
    void initialize() {

    }

    @FXML
    void viewSheetButtonAction(ActionEvent event) {
        String currentSheetName = dashboardController.selectedSheetNameProperty().getValue();

        String finalUrl = HttpUrl
                .parse(Constants.GET_SHEET_PATH)
                .newBuilder()
                .addQueryParameter("sheetName", currentSheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() != 200) {
                    JsonObject responseContent = JsonParser.parseString(responseBody).getAsJsonObject();
                    Platform.runLater(() -> dashboardController.handleError(
                            "Error", null, responseContent.get("error").getAsString()));
                } else {
                    SheetDTO requestedSheet = ADAPTED_GSON.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> dashboardController.handleViewSheet(requestedSheet));
                }
            }
        });
    }

    @FXML
    void requestPermissionButtonAction(ActionEvent event) {
        URL permissionRequestDialogUrl = getClass().getResource(Constants.PERMISSION_REQUEST_DIALOG_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(permissionRequestDialogUrl);
            Parent root = fxmlLoader.load();

            PermissionRequestController dialogController = fxmlLoader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Permission Request");
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(dashboardController.getPrimaryStage());
            dialogStage.showAndWait();

            PermissionType requestedPermission = dialogController.getRequestedPermission();

            if (requestedPermission != null) {
                handlePermissionRequestToOwner(requestedPermission);
            }
        } catch (IOException e) {
            throw new RuntimeException("IO Exception occurred...");
        }
    }

    private void handlePermissionRequestToOwner(PermissionType requestedPermission) {
        String currentSheetName = dashboardController.selectedSheetNameProperty().getValue();

        String finalUrl = HttpUrl
                .parse(Constants.REQUEST_PERMISSION)
                .newBuilder()
                .addQueryParameter("sheetName", currentSheetName)
                .addQueryParameter("permissionType", requestedPermission.toString())
                .build()
                .toString();

        HttpClientUtil.runAsyncPost(finalUrl, RequestBody.create("", null), new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() != 200) {
                    JsonObject responseContent = JsonParser.parseString(responseBody).getAsJsonObject();
                    Platform.runLater(() -> dashboardController.handleError(
                            "Error", null, responseContent.get("error").getAsString()));
                } else {
                    Platform.runLater(() -> refreshPermissionRequestsButtonAction(null));
                }
            }
        });
    }

    @FXML
    void rejectButtonAction(ActionEvent event) {
        handleOwnerPermissionRequestDecision(false);
    }

    @FXML
    void approveButtonAction(ActionEvent event) {
        handleOwnerPermissionRequestDecision(true);
    }

    private void handleOwnerPermissionRequestDecision(Boolean approve) {
        String currentSheetName = dashboardController.selectedSheetNameProperty().getValue();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("requestId", dashboardController.requestIDProperty().getValue());
        jsonObject.addProperty("sheetName", currentSheetName);
        jsonObject.addProperty("decision", approve);

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            Platform.runLater(() -> refreshPermissionRequestsButtonAction(null));
        };

        HttpClientUtil.runPostAsyncWithJson(Constants.PERMISSION_REQUEST_DECISION, requestBody, responseHandler);
    }


    @FXML
    void refreshPermissionRequestsButtonAction(ActionEvent event) {
        dashboardController.refreshPermissions();
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;

        viewSheetButton.disableProperty().bind(
                dashboardController.isSelectedSheetProperty().not()
                        .or(dashboardController.isUserHasNoPermissionsProperty())
        );

        requestPermissionButton.disableProperty().bind(
                dashboardController.isSelectedSheetProperty().not()
                        .or(dashboardController.isUserOwnerOfSelectedSheetProperty())
        );

        approveButton.disableProperty().bind(
                dashboardController.isSelectedSheetProperty().not()
                        .or(dashboardController.isUserOwnerOfSelectedSheetProperty().not())
                                .or(dashboardController.isPermissionRequestSelectedProperty().not())
        );

        rejectButton.disableProperty().bind(
                dashboardController.isSelectedSheetProperty().not()
                        .or(dashboardController.isUserOwnerOfSelectedSheetProperty().not())
                                .or(dashboardController.isPermissionRequestSelectedProperty().not())
        );

        refreshPermissionRequestsButton.disableProperty().bind(dashboardController.isSelectedSheetProperty().not());
    }
}
