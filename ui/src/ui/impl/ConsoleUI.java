package ui.impl;

import engine.Engine;
import ui.ConsoleCommands;
import ui.UI;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleUI implements UI {
    private Engine spreadsheetEngine;

    @Override
    public void executeProgram() {
        int userSelection;

        displayProgramTitle();

        while (true) {
            displaySpreadsheet();
            userSelection = getInputFromUser();
            handleUserSelection(userSelection);
        }
    }

    private int getInputFromUser() {
        int userSelection;
        Scanner scanner = new Scanner(System.in);

        for(ConsoleCommands command : ConsoleCommands.values()) {
            System.out.println(command.ordinal() + 1 + ": " + command);
        }

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
                System.out.println("Unknown error! Exiting program...");
                throw new RuntimeException(e);
            }
        }

        return userSelection - 1;
    }

    private void handleUserSelection(int userSelection) {
        ConsoleCommands command = ConsoleCommands.values()[userSelection];

        try {
            switch (command) {
                case LOAD_SYSTEM_SETTINGS:
                    loadSystemSettings();
                    break;
                case DISPLAY_SPREADSHEET:
                    displaySpreadsheet();
                    break;
                case DISPLAY_CELL_VALUE:
                    displayCellValue();
                    break;
                case UPDATE_CELL_VALUE:
                    updateCellValue();
                    break;
                case DISPLAY_SPREADSHEET_VERSION:
                    displaySpreadsheetVersion();
                    break;
                case EXIT_SYSTEM:
                    System.exit(0); // LEGITIMATE EXIT?
                    break;
                default:
                    System.out.println("Invalid selection. Please try again."); // SHOULD I?
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadSystemSettings() {

    }

    @Override
    public void displaySpreadsheet() {

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
                "   SSSSSSSSSSSSSSS hhhhhhh                     tttt            iiii                                         lllllll lllllll \n" +
                " SS:::::::::::::::Sh:::::h                  ttt:::t           i::::i                                        l:::::l l:::::l \n" +
                "S:::::SSSSSS::::::Sh:::::h                  t:::::t            iiii                                         l:::::l l:::::l \n" +
                "S:::::S     SSSSSSSh:::::h                  t:::::t                                                         l:::::l l:::::l \n" +
                "S:::::S             h::::h hhhhh      ttttttt:::::ttttttt    iiiiiii     cccccccccccccccc    eeeeeeeeeeee    l::::l  l::::l \n" +
                "S:::::S             h::::hh:::::hhh   t:::::::::::::::::t    i:::::i   cc:::::::::::::::c  ee::::::::::::ee  l::::l  l::::l \n" +
                " S::::SSSS          h::::::::::::::hh t:::::::::::::::::t     i::::i  c:::::::::::::::::c e::::::eeeee:::::eel::::l  l::::l \n" +
                "  SS::::::SSSSS     h:::::::hhh::::::htttttt:::::::tttttt     i::::i c:::::::cccccc:::::ce::::::e     e:::::el::::l  l::::l \n" +
                "    SSS::::::::SS   h::::::h   h::::::h     t:::::t           i::::i c::::::c     ccccccce:::::::eeeee::::::el::::l  l::::l \n" +
                "       SSSSSS::::S  h:::::h     h:::::h     t:::::t           i::::i c:::::c             e:::::::::::::::::e l::::l  l::::l \n" +
                "            S:::::S h:::::h     h:::::h     t:::::t           i::::i c:::::c             e::::::eeeeeeeeeee  l::::l  l::::l \n" +
                "            S:::::S h:::::h     h:::::h     t:::::t    tttttt i::::i c::::::c     ccccccce:::::::e           l::::l  l::::l \n" +
                "SSSSSSS     S:::::S h:::::h     h:::::h     t::::::tttt:::::ti::::::ic:::::::cccccc:::::ce::::::::e         l::::::ll::::::l\n" +
                "S::::::SSSSSS:::::S h:::::h     h:::::h     tt::::::::::::::ti::::::i c:::::::::::::::::c e::::::::eeeeeeee l::::::ll::::::l\n" +
                "S:::::::::::::::SS  h:::::h     h:::::h       tt:::::::::::tti::::::i  cc:::::::::::::::c  ee:::::::::::::e l::::::ll::::::l\n" +
                " SSSSSSSSSSSSSSS    hhhhhhh     hhhhhhh         ttttttttttt  iiiiiiii    cccccccccccccccc    eeeeeeeeeeeeee llllllllllllllll"
        );
        System.out.println();
    }
}
