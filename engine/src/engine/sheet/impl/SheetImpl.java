package engine.sheet.impl;

import dto.CellDTO;
import dto.SheetConverter;
import dto.SheetDTO;
import engine.exception.InvalidCellBoundsException;
import engine.expression.ExpressionUtils;
import engine.expression.api.Expression;
import engine.sheet.api.EffectiveValue;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellImpl;
import engine.sheet.coordinate.Coordinate;

import java.util.HashMap;
import java.util.Map;

public class SheetImpl implements Sheet {
    private String name;
    private int numberOfRows;
    private int numberOfColumns;
    private int rowHeightUnits;
    private int columnWidthUnits;
    private Map<Coordinate, Cell> activeCells;
    private Map<Coordinate, Coordinate> dependentCells; //TODO: probably linkedList of coordinates to preform circle check

    public SheetImpl(String name, int numberOfRows, int numberOfColumns, int rowHeightUnits, int columnWidthUnits,
                     Map<Coordinate, Cell> activeCells, Map<Coordinate, Coordinate> dependentCells) {
        this.name = name;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.rowHeightUnits = rowHeightUnits;
        this.columnWidthUnits = columnWidthUnits;
        this.activeCells = activeCells;
        this.dependentCells = dependentCells;
    }

    @Override
    public int getVersion() {
        return 0; //TODO
    }

    public static void validateCoordinateInbound(Coordinate coordinate, int numberOfRows, int numberOfColumns) {
        if(coordinate.getRow() > numberOfRows || coordinate.getRow() < 0 ||
                coordinate.getColumn() > numberOfColumns || coordinate.getColumn() < 0) {
            throw new InvalidCellBoundsException(coordinate, numberOfRows, numberOfColumns);
        }
    }

    public void resetActiveCells() {
        activeCells.clear();
    }

    @Override
    public Cell getCell(Coordinate coordinate) {
        validateCoordinateInbound(coordinate, numberOfRows, numberOfColumns);
        Cell cell = activeCells.get(coordinate);

        if(cell == null) {
            activeCells.put(coordinate, new CellImpl());
        }

        return cell;
    }

    @Override
    public void updateCell(Coordinate coordinate, String value) {
        Cell cell = getCell(coordinate);
        cell.setCellOriginalValue(value);

        EffectiveValue effectiveValue =
                ExpressionUtils.buildExpressionFromString(value)
                        .evaluate(
                                SheetConverter.convertToDTO(this));

        cell.setEffectiveValue(effectiveValue);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setRowHeightUnits(int rowHeightUnits) {
        this.rowHeightUnits = rowHeightUnits;
    }

    @Override
    public void setColumnWidthUnits(int columnWidthUnits) {
        this.columnWidthUnits = columnWidthUnits;
    }

    @Override
    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    @Override
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }

    @Override
    public Map<Coordinate, Coordinate> getDependentCells() {
        return dependentCells;
    }

    @Override
    public int getRowHeightUnits() {
        return rowHeightUnits;
    }

    @Override
    public int getColumnWidthUnits() {
        return columnWidthUnits;
    }

    @Override
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    @Override
    public int getNumberOfRows() {
        return numberOfRows;
    }
}
