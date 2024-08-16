package ui.impl;

import dto.*;
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
            displayMenu();
            userSelection = getInputFromUser();

            if (ConsoleCommands.values()[userSelection] == ConsoleCommands.EXIT_SYSTEM) {
                break;
            }

            handleUserSelection(userSelection);
        }

        System.out.println("Exiting shticell...");
    }

    private void displayMenu() {
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
                System.out.println("Unknown error!");
                throw new RuntimeException(e);
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

    }

    @Override
    public void displaySpreadsheet() {
        SpreadsheetDTO spreadsheet = spreadsheetEngine.getSpreadsheet();
        char columnName = 'A';

        System.out.println("Spreadsheet version: " + spreadsheet.getCurrentVersion());
        System.out.println("Spreadsheet name: " + spreadsheet.getName());
        System.out.println();

        for(int i = 0 ; i < spreadsheet.width() ; i++) {
            System.out.print(" " + (char) (columnName + 1) + " ");
        }

        for(int i = 0 ; i < spreadsheet.height() ; i++) {
            for(int j = 0 ; j < spreadsheet.width() + 1 ; j++) {
                if (j == 0) {
                    System.out.printf("%2d", i);
                }

                System.out.print(" | " + spreadsheetEngine.getCellValue(i, j));
            }

            System.out.println();
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
