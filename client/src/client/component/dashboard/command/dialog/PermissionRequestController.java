package client.component.dashboard.command.dialog;

import dto.permission.PermissionType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class PermissionRequestController {
    @FXML private Button cancelButton;
    @FXML private ToggleGroup permissions;
    @FXML private RadioButton readerRadioButton;
    @FXML private Button sendButton;
    @FXML private RadioButton writerRadioButton;

    private PermissionType requestedPermission;

    @FXML
    void initialize() {
        sendButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendButton.fire();
            }
        });

        cancelButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });

        cancelButton.setOnAction(event -> close());
    }

    @FXML
    void sendButtonAction(ActionEvent event) {
        if(readerRadioButton.isSelected()) {
            requestedPermission = PermissionType.READER;
        }
        else if(writerRadioButton.isSelected()) {
            requestedPermission = PermissionType.WRITER;
        }

        close();
    }

    private void close() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public PermissionType getRequestedPermission() {
        return requestedPermission;
    }
}
