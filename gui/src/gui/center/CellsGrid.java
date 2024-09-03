package gui.center;

import dto.CellDTO;
import dto.SheetDTO;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class CellsGrid extends GridPane {

    public CellsGrid(SheetDTO sheet) {
        initializeGrid(sheet);
    }

    public void initializeGrid(SheetDTO sheet) {
        int numOfRows = sheet.numOfRows();
        int numOfColumns = sheet.numOfColumns();
        int rowsUnits = sheet.rowHeightUnits();
        int columnUnits = sheet.columnWidthUnits();

        setGridLinesVisible(true); // Optional: for debugging

        getChildren().clear(); // Clear existing grid cells

        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfColumns; col++) {
                Label cell = new Label("BLA");

                //CellDTO cell = spreadsheet.activeCells().get(coordinate);
                //String valueString = (cell != null ? formatValueFromSheet(cell.effectiveValue()) : "");


                cell.setPrefWidth(70); // Set preferred width
                cell.setPrefHeight(30); // Set preferred height

                // Add any listeners or custom cell configuration here
                add(cell, col, row);
            }
        }
    }
}
