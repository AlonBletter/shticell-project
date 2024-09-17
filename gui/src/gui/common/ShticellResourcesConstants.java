package gui.common;

import java.net.URL;

public class ShticellResourcesConstants {
    public static final String HEADER_FXML_RESOURCE_IDENTIFIER = "/gui/header/header.fxml";
    public static final String APP_FXML_RESOURCE_IDENTIFIER = "/gui/app/app.fxml";
    public static final String CELL_FXML_RESOURCE_IDENTIFIER = "/gui/singlecell/singlecell.fxml";
    public static final String LOADING_DIALOG_FXML_RESOURCE_IDENTIFIER = "/gui/task/loadingDialog.fxml";
    public static final String DIMENSION_DIALOG_FXML_RESOURCE_IDENTIFIER = "/gui/left/dimensiondialog/dimensionDialog.fxml";

    public static final URL CELL_FXML_URL = ShticellResourcesConstants.class.getResource(CELL_FXML_RESOURCE_IDENTIFIER);
    public static final URL LOADING_DIALOG_URL = ShticellResourcesConstants.class.getResource(LOADING_DIALOG_FXML_RESOURCE_IDENTIFIER);
    public static final URL DIMENSION_DIALOG_URL = ShticellResourcesConstants.class.getResource(DIMENSION_DIALOG_FXML_RESOURCE_IDENTIFIER);
}
