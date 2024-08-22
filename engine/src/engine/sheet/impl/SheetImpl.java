package engine.sheet.impl;

import dto.SheetConverter;
import engine.exception.InvalidCellBoundsException;
import engine.expression.ExpressionUtils;
import engine.generated.STLCell;
import engine.generated.STLCells;
import engine.generated.STLSheet;
import engine.sheet.api.EffectiveValue;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellImpl;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

import java.util.*;

public class SheetImpl implements Sheet {
    private String name;
    private int numberOfRows;
    private int numberOfColumns;
    private int rowHeightUnits;
    private int columnWidthUnits;
    private Map<Coordinate, Cell> activeCells;
    private Map<Coordinate, List<Coordinate>> cellDependents;
    private Map<Coordinate, List<Coordinate>> cellReferences;

    public SheetImpl(String name, int numberOfRows, int numberOfColumns, int rowHeightUnits, int columnWidthUnits,
                     Map<Coordinate, Cell> activeCells,
                     Map<Coordinate, List<Coordinate>> cellDependents,
                     Map<Coordinate, List<Coordinate>> cellReferences) {
        this.name = name;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.rowHeightUnits = rowHeightUnits;
        this.columnWidthUnits = columnWidthUnits;
        this.activeCells = activeCells;
        this.cellDependents = cellDependents;
        this.cellReferences = cellReferences;
    }

    public SheetImpl() {
        activeCells = new HashMap<>();
        cellDependents = new HashMap<>();
        cellReferences = new HashMap<>();
    }

    @Override
    public int getVersion() {
        return 0; //TODO
    }

    public void validateCoordinateInbound(Coordinate coordinate) {
        if(coordinate.getRow() > numberOfRows || coordinate.getRow() < 1 ||
                coordinate.getColumn() > numberOfColumns || coordinate.getColumn() < 1) {
            throw new InvalidCellBoundsException(coordinate, numberOfRows, numberOfColumns);
        }
    }

    @Override
    public Cell getCell(Coordinate coordinate) {
        validateCoordinateInbound(coordinate);

        return activeCells.computeIfAbsent(coordinate, k -> new CellImpl());
    }

    @Override
    public void updateCell(Coordinate coordinate, String newOriginalValue) {
        Cell cellToUpdate = getCell(coordinate);
        String backupOriginalValue = cellToUpdate.getOriginalValue();
        EffectiveValue backupEffectiveValue = cellToUpdate.getEffectiveValue();

        try {
            cellToUpdate.setOriginalValue(newOriginalValue);
            updateSheetEffectiveValues();
        } catch (Exception e) { // Roll-back
            cellToUpdate.setOriginalValue(backupOriginalValue);
            cellToUpdate.setEffectiveValue(backupEffectiveValue);
            throw e;
        }
    }

    @Override
    public void init(STLSheet sheetToInitFrom) {
        setName(sheetToInitFrom.getName());
        setNumberOfRows(sheetToInitFrom.getSTLLayout().getRows());
        setNumberOfColumns(sheetToInitFrom.getSTLLayout().getColumns());
        setRowHeightUnits(sheetToInitFrom.getSTLLayout().getSTLSize().getRowsHeightUnits());
        setColumnWidthUnits(sheetToInitFrom.getSTLLayout().getSTLSize().getColumnWidthUnits());

        STLCells cellsFromFile = sheetToInitFrom.getSTLCells();
        List<STLCell> cellsList = cellsFromFile.getSTLCell();

        for (STLCell stlCell : cellsList) {
            Coordinate cellCoordinate = CoordinateFactory.createCoordinate(stlCell.getRow(), stlCell.getColumn());
            Cell cell = getCell(cellCoordinate);
            cell.setOriginalValue(stlCell.getSTLOriginalValue());
            activeCells.put(cellCoordinate, cell);
        }

        updateSheetEffectiveValues();
    }

    private void updateSheetEffectiveValues() {
        Map<Coordinate, List<Coordinate>> dependencyGraph = createDependencyGraph();
        List<Coordinate> effectiveValueCalculationOrder = getEffectiveValueCalculationOrder(dependencyGraph);

        for(Coordinate coordinate : effectiveValueCalculationOrder) {
            updateCellEffectiveValue(getCell(coordinate));
        }

        cellDependents = dependencyGraph;
        updateReferenceGraph();
    }

    private void updateCellEffectiveValue(Cell cellToUpdate) {
        EffectiveValue newEffectiveValue =
                ExpressionUtils.buildExpressionFromString(cellToUpdate.getOriginalValue())
                        .evaluate(
                                SheetConverter.convertToDTO(this));

        cellToUpdate.setEffectiveValue(newEffectiveValue);
    }

    Map<Coordinate, List<Coordinate>> createDependencyGraph() {
        Map<Coordinate, List<Coordinate>> dependencyGraph = new HashMap<>();

        for (Map.Entry<Coordinate, Cell> entry : activeCells.entrySet()) {
            if (!dependencyGraph.containsKey(entry.getKey())) {
                dependencyGraph.put(entry.getKey(), new LinkedList<>());
            }

            List<Coordinate> currentCellReferences = ExpressionUtils.extractReferences(entry.getValue().getOriginalValue());

            for (Coordinate coordinate : currentCellReferences) {
                // Adding an inactive cell to the dependency graph
                if(!dependencyGraph.containsKey(coordinate)) {
                    dependencyGraph.put(coordinate, new LinkedList<>());
                }

                // Get the list of cells that reference this coordinate
                List<Coordinate> referencesList = dependencyGraph.get(coordinate);

                // Add the current cell coordinate to the list of references
                referencesList.add(entry.getKey());
            }
        }

        return dependencyGraph;
    }

    private void updateReferenceGraph() {
        for (Map.Entry<Coordinate, List<Coordinate>> entry : cellDependents.entrySet()) {
            Coordinate dependent = entry.getKey();
            List<Coordinate> references = entry.getValue();

            for (Coordinate reference : references) {
                cellReferences.computeIfAbsent(reference, k -> new LinkedList<>()).add(dependent);
            }
        }
    }

    private List<Coordinate> getEffectiveValueCalculationOrder(Map<Coordinate, List<Coordinate>> dependencyGraph) {
        List<Coordinate> sortedList = new ArrayList<>();
        Map<Coordinate, Integer> inDegree = new HashMap<>();
        Queue<Coordinate> queue = new LinkedList<>();

        // Step 1: Calculate in-degree for each node
        for (Coordinate node : dependencyGraph.keySet()) {
            inDegree.putIfAbsent(node, 0); // Ensure all nodes are in the in-degree map

            for (Coordinate dependent : dependencyGraph.get(node)) {
                inDegree.put(dependent, inDegree.getOrDefault(dependent, 0) + 1);
            }
        }

        // Step 2: Add all nodes with in-degree 0 to the queue
        for (Map.Entry<Coordinate, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Step 3: Process nodes in queue
        while (!queue.isEmpty()) {
            Coordinate node = queue.poll();
            sortedList.add(node);

            if (dependencyGraph.containsKey(node)) {
                for (Coordinate dependent : dependencyGraph.get(node)) {
                    inDegree.put(dependent, inDegree.get(dependent) - 1);

                    if (inDegree.get(dependent) == 0) {
                        queue.add(dependent);
                    }
                }
            }
        }

        // Step 4: Check for cycles (i.e., if sortedList doesn't contain all nodes)
        if (sortedList.size() != inDegree.size()) {
            throw new IllegalArgumentException("Invalid usage of REF function! Circular reference detected");
        }

        return sortedList;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
    public void setRowHeightUnits(int rowHeightUnits) {
        this.rowHeightUnits = rowHeightUnits;
    }

    @Override
    public void setColumnWidthUnits(int columnWidthUnits) {
        this.columnWidthUnits = columnWidthUnits;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    @Override
    public int getNumberOfRows() {
        return numberOfRows;
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
    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }

    @Override
    public Map<Coordinate, List<Coordinate>> getCellDependents() {
        return cellDependents;
    }

    public Map<Coordinate, List<Coordinate>> getCellReferences() {
        return cellReferences;
    }
}
