package ui;

import ui.api.UI;
import ui.impl.ConsoleUI;

public class ShticellMain {
    public static void main(String[] args) {
        UI shticell = new ConsoleUI();

        shticell.executeProgram();
    }
}
