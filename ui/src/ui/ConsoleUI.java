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
            command.invoke(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadSystemSettings() {
        String userInput;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please enter the absolute file path of the .XML file you'd like to load (Or enter 'q'/'Q' return to the main menu): ");

            try {
                userInput = scanner.next();

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
                "Expected column between A-" + sheetColumnRange + " and row between 1-" + sheetNumOfRows +
                " But received column: " + cellColumnChar + ", row: " + coordinate.getRow());
    }

    @Override
    public void displaySpreadsheet() {
        try {
            SheetDTO spreadsheet = spreadsheetEngine.getSpreadsheet();
            int columnWidth = spreadsheet.columnWidthUnits();

            System.out.println("Displaying the current sheet state:");
            //System.out.println("version #" + spreadsheet.getVersion()); //TODO version feature
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
                    CellDTO cell = spreadsheetEngine.getCell(coordinate);

                    String valueString = formatValueFromSheet(cell.effectiveValue());
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

    @Override
    public void displayCellValue() {
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

    @Override
    public void updateCellValue() {
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

    private void printBasicCellInformation(Coordinate cellToDisplay) {
        CellDTO cellToDisplayDTO = spreadsheetEngine.getCell(cellToDisplay);

        System.out.println("Cell identifier: " + cellToDisplay);
        System.out.println("Cell original value: " + cellToDisplayDTO.effectiveValue().getValue());
        System.out.println("Cell effective value: " + cellToDisplayDTO.effectiveValue().getValue());
    }

    private void printAdvancedCellInformation(Coordinate cellToDisplay) {
        SheetDTO sheetDTO = spreadsheetEngine.getSpreadsheet();
        //TODO +last version modified
        printCoordinates("Cell dependents:", sheetDTO.cellDependents().get(cellToDisplay));
        printCoordinates("Cell references:", sheetDTO.cellReferences().get(cellToDisplay));
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

    @Override
    public void displaySpreadsheetVersion() {

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
