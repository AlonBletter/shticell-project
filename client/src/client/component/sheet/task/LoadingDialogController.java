package client.component.sheet.task;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class LoadingDialogController {

    @FXML private Label loadingFileTitleLabel;
    @FXML private Label loadingStatusLabel;
    @FXML private Label loadingMessageLabel;
    @FXML private Label loadingPrecentLabel;
    @FXML private ProgressBar loadingProgressBar;
    @FXML private Button closeButton;

    @FXML
    private void initialize() {
        closeButton.setDisable(true);
        closeButton.setOnAction(event -> closeButton.getScene().getWindow().hide());
    }

    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        loadingMessageLabel.textProperty().bind(aTask.messageProperty());
        loadingProgressBar.progressProperty().bind(aTask.progressProperty());
        loadingPrecentLabel.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        aTask.progressProperty(),
                                        100)),
                        " %"));

        aTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onTaskFinished(Optional.ofNullable(onFinish));
        });
    }

    public void setLoadingFileTitleLabel(String filePath) {
        Path path = Paths.get(filePath);
        loadingFileTitleLabel.setText("Loading " + path.getFileName().toString() + " file...");
    }

    public void onTaskFinished(Optional<Runnable> onFinish) {
        this.loadingMessageLabel.textProperty().unbind();
        this.loadingPrecentLabel.textProperty().unbind();
        this.loadingProgressBar.progressProperty().unbind();
        closeButton.setDisable(false);
        onFinish.ifPresent(Runnable::run);
    }
}
