package client.component.sheet.left.dialog.whatif;

import client.component.sheet.app.SheetController;
import client.component.sheet.common.ShticellResourcesConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class MultipleWhatIfController {
    @FXML private Button addCellButton;
    @FXML private VBox dialogContainer;
    @FXML private Button removeCellButton;

    private SheetController mainController;
    private Stage dialogStage;

    @FXML
    private void initialize() {
    }

    @FXML
    void addCellButtonListener() {
        try {
            FXMLLoader loader = new FXMLLoader(ShticellResourcesConstants.WHAT_IF_DIALOG_URL);
            Pane dialogPane = loader.load();
            WhatIfDialogController dialogController = loader.getController();
            dialogController.setMainController(mainController);
            dialogController.setDialogStage(dialogStage);
            dialogContainer.getChildren().add(dialogPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void removeCellButtonListener(ActionEvent event) {
        if (!dialogContainer.getChildren().isEmpty()) {
            // Remove the last added pane
            dialogContainer.getChildren().remove(dialogContainer.getChildren().size() - 1);
        }
    }


    public void setMainController(SheetController mainController) {
        this.mainController = mainController;
    }
    public void setDialogStage(Stage dialogStage) {this.dialogStage = dialogStage;}
}
