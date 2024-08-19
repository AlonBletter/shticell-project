package ui.impl;

import dto.CellDTO;
import dto.SheetDTO;
import engine.Engine;
import engine.EngineImpl;
import engine.exception.DataReadException;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import ui.ConsoleCommands;
import ui.UI;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI implements UI {
    private final Engine spreadsheetEngine = new EngineImpl();
    private boolean systemIsLoaded = false;

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
            System.out.print("Please enter the absolute file path of the .XML file (Or enter q/Q return to the main menu): ");

            try {
                userInput = scanner.next();

                if(userInput.equalsIgnoreCase("q")) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                spreadsheetEngine.loadSystemSettingsFromFile(userInput);
                systemIsLoaded = true;
                System.out.println("Loaded file successfully...");
                break;
            } catch (Exception e) {
                System.out.println("Error! " + e.getMessage());
            }
        }
    }

    @Override
    public void displaySpreadsheet() {
        try {
            SheetDTO spreadsheet = spreadsheetEngine.getSpreadsheet();
            Map<Coordinate, CellDTO> cells = spreadsheet.activeCells();
            int columnWidth = spreadsheet.columnWidthUnits();

            //System.out.println("Spreadsheet version: " + spreadsheet.getVersion()); //TODO version
            System.out.println("Spreadsheet name: " + spreadsheet.name());
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
                    Object cellValue = cell != null ? cell.effectiveValue().getValue() : ""; //TODO is object fine? (we assume its primitive inside)

                    System.out.printf(" | %-" + columnWidth + "s", cellValue);
                }

                System.out.println("|");
            }
        } catch (Exception e) {
            System.out.println("Error! +" + e.getMessage());
        }
    }

    @Override
    public void displayCellValue() {

    }

    @Override
    public void updateCellValue() {

    }

    @Override
    public void displaySpreadsheetVersion() {

    }

    private void displayProgramTitle() {
        System.out.println(
                "---------------------------------\n" +
                "-      Welcome To Shticell      -\n" +
                "---------------------------------"
        );
    }
}
