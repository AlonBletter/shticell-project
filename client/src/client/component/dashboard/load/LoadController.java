package client.component.dashboard.load;

import client.component.dashboard.DashboardController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.util.function.Consumer;

public class LoadController {


    @FXML private Label usernameLabel;

    private DashboardController dashboardController;

    @FXML
    void initialize() {

    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(dashboardController.getPrimaryStage());
        if (selectedFile == null) {
            return;
        }

        RequestBody fileBody = RequestBody.create(selectedFile, MediaType.parse("application/xml"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", selectedFile.getName(), fileBody)
                .build();

        Consumer<String> responseHandler = (response) -> Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Load File");
            alert.setHeaderText(null);
            alert.setContentText("The sheet was loaded successfully.");
            alert.showAndWait();
        });

        HttpClientUtil.runReqAsyncWithJson(Constants.LOAD_SHEET_PATH, HttpMethod.POST, requestBody, responseHandler);
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void updateUsernameLabel(String username) {
        usernameLabel.setText(username);
    }
}
