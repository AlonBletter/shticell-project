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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SheetImpl implements Sheet {
    private String name;
    private int numberOfRows;
    private int numberOfColumns;
    private int rowHeightUnits;
    private int columnWidthUnits;
    private Map<Coordinate, Cell> activeCells;
    private Map<Coordinate, List<Coordinate>> dependentCells;

    public SheetImpl(String name, int numberOfRows, int numberOfColumns, int rowHeightUnits, int columnWidthUnits,
                     Map<Coordinate, Cell> activeCells, Map<Coordinate, List<Coordinate>> dependentCells) {
        this.name = name;
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.rowHeightUnits = rowHeightUnits;
        this.columnWidthUnits = columnWidthUnits;
        this.activeCells = activeCells;
        this.dependentCells = dependentCells;
    }

    public SheetImpl() {
        //TODO maybe new to data structures members
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
        activeCells = new HashMap<>();

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

        dependentCells = dependencyGraph;
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

            List<Coordinate> currentCellReferences = extractReferences(entry.getValue().getOriginalValue());

            for (Coordinate coordinate : currentCellReferences) {
                // Adding an inactive cell to the dependency graph
                if(!dependencyGraph.containsKey(coordinate)) {
                    dependencyGraph.put(coordinate, new LinkedList<>());
                }

                // Get the list of cells that reference this coordinate
                List<Coordinate> referencesList = dependencyGraph.get(coordinate);

                // Add the current cell coordinate to the list of references
                referencesList.add(entry.getKey());

//                // Update the map with the new or updated list of references
//                dependencyGraph.put(coordinate, referencesList);
            }
        }

        return dependencyGraph;
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
            throw new IllegalArgumentException("Circular reference detected");
        }

        return sortedList;
    }

    //TODO is this the best place to put the method? its too coupled to the sheet
    private List<Coordinate> extractReferences(String input) {
        List<Coordinate> coordinates = new ArrayList<>();

        // Use regex to match the pattern {REF,Coordinate}
        String regex = "\\{REF,([A-Z]+\\d+)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // Find all matches and add them to the list
        while (matcher.find()) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(matcher.group(1));
            coordinates.add(coordinate); // group(1) gets the coordinate part (e.g., A4, A5)
        }

        return coordinates;
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
    public Map<Coordinate, List<Coordinate>> getDependentCells() {
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
