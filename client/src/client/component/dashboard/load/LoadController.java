package client.component.dashboard.load;

import client.component.main.AppController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class LoadController {

    @FXML private GridPane loadComponent;
    @FXML private Button loadFileButton;
    @FXML private Label loadedFilePathLabel;
    @FXML private Label usernameLabel;

    private AppController mainController;

    @FXML
    void initialize() {

    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(mainController.getPrimaryStage());
        if (selectedFile == null) {
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOAD_SHEET_PATH)
                .newBuilder()
                .build()
                .toString();

        RequestBody fileBody = RequestBody.create(selectedFile, MediaType.parse("application/xml"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", selectedFile.getName(), fileBody)
                .build();

        HttpClientUtil.runAsyncPost(finalUrl, requestBody, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> //TODO setDashboard and not mainController! and then preset the error
                        mainController.showErrorAlert("HTTP request error",
                                "An error occurred while trying to send a load sheet HTTP request.", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() != 200) {
                    Platform.runLater(() ->
                            mainController.showErrorAlert("File Loading Error",
                                    "An error occurred while opening the file dialog", responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        //TODO need something? print successful
                    });
                }
            }
        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        //TODO maybe pass the name label to the header border in the main app
        usernameLabel.textProperty().bind(Bindings.concat("Logged As ", mainController.usernameProperty()));
    }
}
