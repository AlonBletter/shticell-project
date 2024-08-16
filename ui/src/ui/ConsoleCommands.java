package ui;

public enum ConsoleCommands {

    LOAD_SYSTEM_SETTINGS("Load system settings"),
    DISPLAY_SPREADSHEET("Display spreadsheet"),
    DISPLAY_CELL_VALUE("Display cell value"),
    UPDATE_CELL_VALUE("Update cell value"),
    DISPLAY_SPREADSHEET_VERSION("Display spreadsheet version"),
    EXIT_SYSTEM("Exit system");

    private String name;

    ConsoleCommands(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
