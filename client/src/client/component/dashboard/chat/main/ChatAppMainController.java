package client.component.dashboard.chat.main;

import client.component.dashboard.chat.chatroom.ChatRoomMainController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import static client.util.Constants.CHAT_ROOM_FXML_RESOURCE_LOCATION;

public class ChatAppMainController implements Closeable {

    private Parent chatRoomComponent;
    private ChatRoomMainController chatRoomComponentController;

    @FXML private Label userGreetingLabel;
    @FXML private AnchorPane mainPanel;

    private final StringProperty currentUserName;

    public ChatAppMainController() {
        currentUserName = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));

        loadChatRoomPage();
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }

    @Override
    public void close() {
        chatRoomComponentController.close();
    }

    private void loadChatRoomPage() {
        URL chatRoomPage = getClass().getResource(CHAT_ROOM_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(chatRoomPage);
            chatRoomComponent = fxmlLoader.load();
            chatRoomComponentController = fxmlLoader.getController();
            chatRoomComponentController.setChatAppMainController(this);
            chatRoomComponentController.setActive();
            AnchorPane.setBottomAnchor(chatRoomComponent, 1.0);
            AnchorPane.setTopAnchor(chatRoomComponent, 1.0);
            AnchorPane.setLeftAnchor(chatRoomComponent, 1.0);
            AnchorPane.setRightAnchor(chatRoomComponent, 1.0);
            mainPanel.getChildren().add(chatRoomComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
