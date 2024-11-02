package client.component.dashboard.chat.commands;


import client.component.dashboard.chat.chatroom.ChatRoomMainController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class ChatCommandsController {

    private ChatRoomMainController chatCommands;
    private final BooleanProperty autoUpdates;
    @FXML private ToggleButton autoUpdatesButton;
    @FXML private Button quitButton;

    public ChatCommandsController() {
        autoUpdates = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        autoUpdates.bind(autoUpdatesButton.selectedProperty());
    }

    public ReadOnlyBooleanProperty autoUpdatesProperty() {
        return autoUpdates;
    }

    @FXML
    void quitClicked(ActionEvent event) {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        chatCommands.setInActive();
        stage.close();
    }

    public void setChatCommands(ChatRoomMainController chatRoomMainController) {
        this.chatCommands = chatRoomMainController;
    }
}
