package client.component.dashboard.load;

import client.component.main.AppController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
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
import java.util.function.Consumer;

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

        RequestBody fileBody = RequestBody.create(selectedFile, MediaType.parse("application/xml"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", selectedFile.getName(), fileBody)
                .build();

        Consumer<String> responseHandler = (response) -> {
            //TODO need something? print successful
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.LOAD_SHEET_PATH, HttpMethod.POST, requestBody, responseHandler);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        //TODO maybe pass the name label to the header border in the main app
        usernameLabel.textProperty().bind(Bindings.concat("Logged As ", mainController.usernameProperty()));
    }
}
