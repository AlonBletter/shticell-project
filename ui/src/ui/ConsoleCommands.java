package ui;

public enum ConsoleCommands {

    LOAD_SYSTEM_FROM_XML_FILE("Load system from .xml file"),
    DISPLAY_SPREADSHEET("Display spreadsheet"),
    DISPLAY_CELL_VALUE("Display cell value"),
    UPDATE_CELL_VALUE("Update cell value"),
    DISPLAY_SPREADSHEET_VERSION("Display spreadsheet version"),
    SAVE_SYSTEM_TO_FILE("Save system current data to a file"),
    LOAD_SYSTEM_FROM_FILE("Load system data from a file"),
    EXIT_SYSTEM("Exit system");

    private final String description;

    ConsoleCommands(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
