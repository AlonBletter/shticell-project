package client.component.login;

import client.component.main.AppController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class LoginController {

    @FXML private Button exitButton;
    @FXML private Button loginButton;
    @FXML private ProgressBar loginProgressBar;
    @FXML private Label errorMessageLabel;
    @FXML private TextField userNameTextField;

    private SimpleStringProperty errorMessageProperty = new SimpleStringProperty();
    private AppController mainController;

    @FXML
    void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void loginButtonClicked(ActionEvent event) {
        String username = userNameTextField.getText();
        if (username.isEmpty()) {
            errorMessageProperty.set("Username is empty. You can't login with empty username");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PATH)
                .newBuilder()
                .addQueryParameter("username", username)
                .build()
                .toString();

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                Platform.runLater(() -> {
                    mainController.updateUsername(username);
                    mainController.loadDashboardScreen();
                });
            }
        };

        HttpClientUtil.runReqAsyncWithJson(finalUrl, HttpMethod.GET, null, responseHandler);
    }
}
