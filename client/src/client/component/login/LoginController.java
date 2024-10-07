package client.component.login;

import client.component.main.AppController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
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
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", username)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        mainController.updateUsername(username);
                        mainController.loadDashboardScreen();
                    });
                }
            }
        });
    }
}
