package ui;

import dto.CellDTO;
import dto.SheetDTO;
import engine.Engine;
import engine.EngineImpl;
import engine.exception.InvalidCellBoundsException;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI implements UI {
    private final Engine spreadsheetEngine = new EngineImpl();

    @Override
    public void executeProgram() {
        int userSelection;

        displayProgramTitle();

        while (true) {
            displayMenu();
            userSelection = getInputFromUser();

            if (ConsoleCommands.values()[userSelection] == ConsoleCommands.EXIT_SYSTEM) {
                break;
            }

            handleUserSelection(userSelection);
            System.out.println();
        }

        System.out.println("Exiting shticell...");
    }

    private void displayMenu() {
        System.out.println("Menu Options:");
        System.out.println("-------------");
        for(ConsoleCommands command : ConsoleCommands.values()) {
            System.out.println(command.ordinal() + 1 + ": " + command);
        }
    }

    private int getInputFromUser() {
        int userSelection;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please select your option by entering the command number: ");

            try {
                userSelection = scanner.nextInt();

                if(userSelection <= 0 || userSelection >= ConsoleCommands.values().length + 1) {
                    System.out.println("Invalid option! Please enter one of the options above.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                scanner.next();
                System.out.println("Invalid input! Please enter a number corresponding to your choice.");
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage());
            }
        }

        System.out.println();

        return userSelection - 1;
    }

    private void handleUserSelection(int userSelection) {
        ConsoleCommands command = ConsoleCommands.values()[userSelection];

        try {
            switch (command) {
                case ConsoleCommands.LOAD_SYSTEM_FROM_XML_FILE -> loadSystemSettings();
                case ConsoleCommands.DISPLAY_SPREADSHEET -> displaySpreadsheet();
                case ConsoleCommands.DISPLAY_CELL_VALUE -> displayCellInformation();
                case ConsoleCommands.UPDATE_CELL_VALUE -> updateCellValue();
                case ConsoleCommands.DISPLAY_SPREADSHEET_VERSION -> displaySpreadsheetVersion();
                case ConsoleCommands.SAVE_SYSTEM_TO_FILE -> saveSystemFromFile();
                case ConsoleCommands.LOAD_SYSTEM_FROM_FILE -> loadSystemFromFile();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void loadSystemSettings() {
        String userInput;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please enter the absolute file path of the .XML file you'd like to load (Or enter 'q'/'Q' return to the main menu): ");

            try {
                userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("q")) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                spreadsheetEngine.loadSystemSettingsFromFile(userInput);
                System.out.println("Loaded file successfully...");
                break;
            } catch (InvalidCellBoundsException e) {
                handleInvalidCellBoundException(e);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    private void handleInvalidCellBoundException(InvalidCellBoundsException e) {
        Coordinate coordinate = e.getActualCoordinate();
        int sheetNumOfRows = e.getSheetNumOfRows();
        int SheetNumOfColumns = e.getSheetNumOfColumns();
        char sheetColumnRange = (char) (SheetNumOfColumns + 'A' - 1);
        char cellColumnChar = (char) (coordinate.getColumn() + 'A' - 1);

        System.out.println(e.getMessage() + "Invalid cell bounds!\n" +
                "Expected column between A-" + sheetColumnRange + " and row between 1-" + sheetNumOfRows + "\n" +
                "But received column [" + cellColumnChar + "] and row [" + coordinate.getRow() + "]");
    }

    private void displaySpreadsheet() {
        printSpreadsheet(spreadsheetEngine.getSpreadsheet());
    }

    private void printSpreadsheet(SheetDTO spreadsheet) {
        try {
            int columnWidth = spreadsheet.columnWidthUnits();

            System.out.println("Displaying the current sheet state:");
            System.out.println("version #" + spreadsheet.versionNum());
            System.out.println("name: " + spreadsheet.name());
            System.out.println();

            // Prints columns headers
            System.out.print("     ");
            for (int i = 0; i < spreadsheet.numOfColumns(); i++) {
                char columnName = (char) ('A' + i);
                System.out.printf("%-" + columnWidth + "s" + "   ", columnName);
            }
            System.out.println();

            for (int i = 0; i < spreadsheet.numOfRows(); i++) {
                // Prints rows headers
                System.out.printf("%2d", i + 1);

                // Prints cells values
                for (int j = 0; j < spreadsheet.numOfColumns(); j++) {
                    Coordinate coordinate = CoordinateFactory.createCoordinate(i + 1, j + 1);
                    CellDTO cell = spreadsheet.activeCells().get(coordinate);
                    String valueString = (cell != null ? formatValueFromSheet(cell.effectiveValue()) : "");

                    if (valueString.length() > columnWidth) {
                        valueString = valueString.substring(0, columnWidth);
                    }

                    System.out.printf(" | %-" + columnWidth + "s", valueString);
                }

                System.out.println("|");
            }
        } catch (Exception e) {
            System.out.println("Error! " + e.getMessage());
        }
    }

    private void displayCellInformation() {
        String userInput;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Please enter the desirable cell identifier (For example: A4) to display (Or enter 'q'/'Q' return to the main menu): ");
                userInput = scanner.nextLine();

                if(userInput.equalsIgnoreCase("q")) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                Coordinate cellToDisplayCoordinate = CoordinateFactory.createCoordinate(userInput);
                printBasicCellInformation(cellToDisplayCoordinate);
                printAdvancedCellInformation(cellToDisplayCoordinate);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printBasicCellInformation(Coordinate cellToDisplay) {
        CellDTO cellToDisplayDTO = spreadsheetEngine.getCell(cellToDisplay);
        EffectiveValue cellEffectiveValue = cellToDisplayDTO.effectiveValue();

        System.out.println("Cell's identifier : " + cellToDisplay);
        System.out.println("Cell's original value: " + cellToDisplayDTO.originalValue());
        System.out.println("Cell's effective value: " + formatValueFromSheet(cellEffectiveValue));
    }

    private void printAdvancedCellInformation(Coordinate cellToDisplay) {
        SheetDTO sheetDTO = spreadsheetEngine.getSpreadsheet();
        int numberOfCellsModifiedInVersion = spreadsheetEngine.getCell(cellToDisplay).lastModifiedVersion();

        System.out.println("The cell was last modified at version #" + numberOfCellsModifiedInVersion);
        printCoordinates("The cells that " + cellToDisplay + " depends on:", sheetDTO.cellReferences().get(cellToDisplay));
        printCoordinates("The cells that depends on " + cellToDisplay + ":", sheetDTO.cellDependents().get(cellToDisplay));
    }

    private void printCoordinates(String label, List<Coordinate> coordinates) {
        System.out.print(label);
        if (coordinates != null && !coordinates.isEmpty()) {
            coordinates.forEach(coordinate -> System.out.print(" " + coordinate));
        } else {
            System.out.print(" None");
        }
        System.out.println();
    }

    private void updateCellValue() {
        String userInput;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Please enter the desirable cell identifier (For example: A4) to update (Or enter 'q'/'Q' return to the main menu): ");
                userInput = scanner.nextLine();

                if(userInput.equalsIgnoreCase("q")) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                Coordinate cellToUpdateCoordinate = CoordinateFactory.createCoordinate(userInput);
                printBasicCellInformation(cellToUpdateCoordinate);
                System.out.print("Please enter the new value of the cell (Or leave empty to clear the cell value): ");
                String newCellValue = scanner.nextLine().trim();
                spreadsheetEngine.updateCell(cellToUpdateCoordinate, newCellValue);
                System.out.println("Cell updated successfully...");
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void displaySpreadsheetVersion() {
        Map<Integer, SheetDTO> versionsDTO = spreadsheetEngine.getSpreadsheet().versions();

        System.out.println("Displaying the versions of the spreadsheet:");
        System.out.printf("%-10s | %-20s%n", "Version #", "Cells Modified");

        for (Map.Entry<Integer, SheetDTO> entry : versionsDTO.entrySet()) {
            int versionNum = entry.getKey();
            SheetDTO sheetDTO = entry.getValue();
            int numberOfCellsModifiedInVersion = sheetDTO.lastModifiedCells().size();

            System.out.printf("%-10d | %-20d%n", versionNum, numberOfCellsModifiedInVersion);
        }

        getVersionToDisplayFromUser(versionsDTO);
    }

    private void getVersionToDisplayFromUser(Map<Integer, SheetDTO> versionsDTO) {
        String userInput;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Please enter the version you'd like to display (Or enter 'q'/'Q' to return to the main menu): ");
                userInput = scanner.nextLine().trim();

                if (userInput.equalsIgnoreCase("q")) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                int versionNumber = Integer.parseInt(userInput);

                if (versionNumber < 1 || versionNumber > versionsDTO.size()) {
                    System.out.println("Invalid option! Please enter a number between 1 and " + versionsDTO.size() + ".");
                } else {
                    printSpreadsheet(versionsDTO.get(versionNumber));
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number corresponding to the versions.");
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage());
            }
        }
    }

    private void saveSystemFromFile() {
        String userInput;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Please enter the absolute file path (Without file extension) of where you'd like to save the system to (Or enter 'q'/'Q' to return to the main menu): ");
                userInput = scanner.nextLine().trim();

                if (userInput.equalsIgnoreCase("q")) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                spreadsheetEngine.writeSystemDataToFile(userInput);
                System.out.println("The system data was saved successfully...");
                break;
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage());
            }
        }
    }

    private void loadSystemFromFile() {
        String userInput;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Please enter the absolute file path (Without file extension) from which the system is saved (Or enter 'q'/'Q' to return to the main menu): ");
                userInput = scanner.nextLine().trim();

                if (userInput.equalsIgnoreCase("q")) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                spreadsheetEngine.readSystemDataFromFile(userInput);
                System.out.println("The system data was loaded successfully...");
                break;
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage());
            }
        }
    }

    private String formatValueFromSheet(EffectiveValue objectFromSheet) {
        String formattedObject = objectFromSheet.getValue().toString();

        if(CellType.NUMERIC == objectFromSheet.getCellType()) {
            double doubleValue = (Double) objectFromSheet.getValue();

            if(doubleValue % 1 == 0) {
                long longValue = (long) doubleValue;

                formattedObject = String.format("%,d", longValue);
            } else {
                formattedObject = String.format("%,.2f", doubleValue);
            }
        } else if(CellType.BOOLEAN == objectFromSheet.getCellType()) {
            boolean booleanValue = (Boolean) objectFromSheet.getValue();

            formattedObject = Boolean.toString(booleanValue).toUpperCase();
        }

        return formattedObject;
    }

    private void displayProgramTitle() {
        System.out.println(
                "---------------------------------\n" +
                "-      Welcome To Shticell      -\n" +
                "---------------------------------\n"
        );
    }
}
