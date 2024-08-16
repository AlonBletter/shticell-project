package ui;

public enum ConsoleCommands {

    LOAD_SYSTEM_SETTINGS("Load system settings") {
        @Override
        public void invoke(UI userInterface) {
            userInterface.loadSystemSettings();
        }
    },
    DISPLAY_SPREADSHEET("Display spreadsheet") {
        @Override
        public void invoke(UI userInterface) {
            userInterface.displaySpreadsheet();
        }
    },
    DISPLAY_CELL_VALUE("Display cell value") {
        @Override
        public void invoke(UI userInterface) {
        userInterface.displayCellValue();
        }
    },
    UPDATE_CELL_VALUE("Update cell value") {
        @Override
        public void invoke(UI userInterface) {
            userInterface.updateCellValue();
        }
    },
    DISPLAY_SPREADSHEET_VERSION("Display spreadsheet version") {
        @Override
        public void invoke(UI userInterface) {
            userInterface.displaySpreadsheetVersion();
        }
    },
    EXIT_SYSTEM("Exit system") {
        @Override
        public void invoke(UI userInterface) {

        }
    };

    private final String description;

    ConsoleCommands(String description) {
        this.description = description;
    }

    public abstract void invoke(UI userInterface);

    @Override
    public String toString() {
        return description;
    }
}
