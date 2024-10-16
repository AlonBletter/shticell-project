package client.component.dashboard.command;

import client.component.dashboard.DashboardController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CellStyleDTO;
import dto.SheetDTO;
import dto.deserializer.CellStyleDTOAdapter;
import dto.deserializer.CoordinateTypeAdapter;
import dto.deserializer.EffectiveValueTypeAdapter;
import dto.deserializer.SheetDTODeserializer;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.effectivevalue.EffectiveValue;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CommandsController {

    @FXML private VBox commandsComponent;
    @FXML private Button permissionManagerButton;
    @FXML private Button requestPermissionButton;
    @FXML private Button viewSheetButton;

    private DashboardController dashboardController;

    @FXML
    void initialize() {
        assert commandsComponent != null : "fx:id=\"commandsComponent\" was not injected: check your FXML file 'commands.fxml'.";
        assert permissionManagerButton != null : "fx:id=\"permissionManagerButton\" was not injected: check your FXML file 'commands.fxml'.";
        assert requestPermissionButton != null : "fx:id=\"requestPermissionButton\" was not injected: check your FXML file 'commands.fxml'.";
        assert viewSheetButton != null : "fx:id=\"viewSheetButton\" was not injected: check your FXML file 'commands.fxml'.";

    }

    @FXML
    void permissionManagerButtonAction(ActionEvent event) {

    }

    @FXML
    void requestPermissionButtonAction(ActionEvent event) {

    }

    @FXML
    void viewSheetButtonAction(ActionEvent event) {
        String currentSheetName = dashboardController.selectedSheetNameProperty().getValue();

        if(currentSheetName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a sheet");
            alert.showAndWait();
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.GET_SHEET_PATH)
                .newBuilder()
                .addQueryParameter("sheetName", dashboardController.selectedSheetNameProperty().getValue())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Platform.runLater(() ->
//                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
//                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() != 200) {
//                    Platform.runLater(() ->
//                            errorMessageProperty.set("Something went wrong: " + responseBody)
//                    );
                } else {
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                            .registerTypeAdapter(CellStyleDTO.class, new CellStyleDTOAdapter())
                            .registerTypeAdapter(Coordinate.class, new CoordinateTypeAdapter())
                            .registerTypeAdapter(EffectiveValue.class ,new EffectiveValueTypeAdapter())
                            .create();

                    SheetDTO requestedSheet = gson.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        dashboardController.handleViewSheet(requestedSheet);
                    });
                }
            }
        });
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
}
