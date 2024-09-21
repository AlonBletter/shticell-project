package engine.sheet.impl;

import engine.exception.InvalidCellBoundsException;
import engine.expression.ExpressionUtils;
import engine.generated.STLCell;
import engine.generated.STLCells;
import engine.generated.STLSheet;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.CellReadActions;
import engine.sheet.cell.impl.CellImpl;
import engine.sheet.cell.impl.EmptyCell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.range.Range;
import engine.sheet.range.RangeImpl;

import java.io.*;
import java.util.*;

public class SheetImpl implements Sheet, Serializable {
    private String name;
    private int numberOfRows;
    private int numberOfColumns;
    private int rowHeightUnits;
    private int columnWidthUnits;
    private final Map<Coordinate, Cell> activeCells;
    private Map<Coordinate, List<Coordinate>> cellInfluenceOn;
    private Map<Coordinate, List<Coordinate>> cellDependsOn;
    private final List<Cell> lastModifiedCells;
    private Map<String, Range> ranges;
    private List<String> activeRanges;
    private int versionNumber;

    public SheetImpl() {
        activeCells = new HashMap<>();
        cellInfluenceOn = new HashMap<>();
        cellDependsOn = new HashMap<>();
        lastModifiedCells = new LinkedList<>();
        ranges = new HashMap<>();
        versionNumber = 1;
    }

    @Override
    public CellReadActions getCell(Coordinate coordinate) {
        validateCoordinateInbound(coordinate);

        Cell cell = activeCells.get(coordinate);

        if(cell == null) {
            activeCells.put(coordinate, EmptyCell.INSTANCE);
            return activeCells.get(coordinate);
        }

        return cell;
    }

     private Cell addNewCellIfEmptyCell(Coordinate coordinate) {
        validateCoordinateInbound(coordinate);
//         CellReadActions cellReadActions = getCell(coordinate);
//
//         if (cellReadActions instanceof EmptyCell) {
//             Cell newCell = new CellImpl(coordinate);
//             activeCells.put(coordinate, newCell);
//             return newCell;
//         }//TODO FIX THIS CASTING

         return activeCells.computeIfAbsent(coordinate, CellImpl::new);
     }

    @Override
    public void updateCell(Coordinate coordinate, String newOriginalValue) {
        Cell cellToUpdate = addNewCellIfEmptyCell(coordinate);

        lastModifiedCells.clear();
        cellToUpdate.setOriginalValue(newOriginalValue);
        updateSheetEffectiveValues();
        versionNumber++;
        //TODO consider return true for indication for the engine so it will know if to add to version manager or not.
        // if so, do it with lastModifiedCells == 0
    }

    @Override
    public void addRange(String rangeName, String rangeCoordinates) {
        List<Coordinate> startEnd = ExpressionUtils.parseRange(rangeCoordinates);
        Coordinate startOfRange = startEnd.get(0);
        Coordinate endOfRange = startEnd.get(1);
        validateCoordinateInbound(startOfRange);
        validateCoordinateInbound(endOfRange);

        if(ranges.containsKey(rangeName)) {
            throw new IllegalArgumentException("Range with the name " + rangeName + " already exists.");
        } else {
            ranges.put(rangeName, new RangeImpl(rangeName, startOfRange, endOfRange));
        }
    }

    @Override
    public void deleteRange(String rangeNameToDelete) {
        if(!ranges.containsKey(rangeNameToDelete)) {
            throw new IllegalArgumentException("Range with the name " + rangeNameToDelete + " does not exist.");
        }

        Range removedRange = ranges.remove(rangeNameToDelete);

        try {
            updateSheetEffectiveValues();
        } catch (Exception e) {
            ranges.put(rangeNameToDelete, removedRange);
            throw new IllegalArgumentException("Range [" + rangeNameToDelete + "] cannot be deleted because it's being used.");
        }
    }

    @Override
    public List<Coordinate> getRange(String rangeNameToView) {
        if (rangeNameToView == null) {
            return null;
        }

        Range range = ranges.get(rangeNameToView);

        if (range == null) {
            throw new IllegalArgumentException("Range with the name [" + rangeNameToView + "] does not exist.");
        }

        return range.getCellsInRange();
    }

    @Override
    public void init(STLSheet sheetToInitFrom) {
        setName(sheetToInitFrom.getName());
        setNumberOfRows(sheetToInitFrom.getSTLLayout().getRows());
        setNumberOfColumns(sheetToInitFrom.getSTLLayout().getColumns());
        setRowHeightUnits(sheetToInitFrom.getSTLLayout().getSTLSize().getRowsHeightUnits());
        setColumnWidthUnits(sheetToInitFrom.getSTLLayout().getSTLSize().getColumnWidthUnits());
        //TODO add loading ranges
        STLCells cellsFromFile = sheetToInitFrom.getSTLCells();
        List<STLCell> cellsList = cellsFromFile.getSTLCell();

        for (STLCell stlCell : cellsList) {
            Coordinate cellCoordinate = CoordinateFactory.createCoordinate(stlCell.getRow(), stlCell.getColumn());
            Cell cell = addNewCellIfEmptyCell(cellCoordinate);
            cell.setOriginalValue(stlCell.getSTLOriginalValue());
            activeCells.put(cellCoordinate, cell);
        }

        updateSheetEffectiveValues();
        versionNumber++;
    }

    private void updateSheetEffectiveValues() {
        Map<Coordinate, List<Coordinate>> dependencyGraph = createDependencyGraph();
        List<Coordinate> effectiveValueCalculationOrder = getEffectiveValueCalculationOrder(dependencyGraph);

        for(Coordinate coordinate : effectiveValueCalculationOrder) {
            if(getCell(coordinate) != EmptyCell.INSTANCE) {
                updateCellEffectiveValue(addNewCellIfEmptyCell(coordinate));
            }
        }

        cellInfluenceOn = dependencyGraph;
        updateReferenceGraph();
    }

    private void updateCellEffectiveValue(Cell cellToUpdate) {
        EffectiveValue newEffectiveValue =
                ExpressionUtils.buildExpressionFromString(cellToUpdate.getOriginalValue())
                        .evaluate(this);

        if(!cellToUpdate.getEffectiveValue().equals(newEffectiveValue)) {
            cellToUpdate.setEffectiveValue(newEffectiveValue);
            cellToUpdate.setLastModifiedVersion(versionNumber);
            lastModifiedCells.add(cellToUpdate);
        }
    }

    // MASHPIA AL
    Map<Coordinate, List<Coordinate>> createDependencyGraph() {
        Map<Coordinate, List<Coordinate>> dependencyGraph = new HashMap<>();

        for (Map.Entry<Coordinate, Cell> entry : activeCells.entrySet()) {
            if (!dependencyGraph.containsKey(entry.getKey())) {
                dependencyGraph.put(entry.getKey(), new LinkedList<>());
            }

            List<Coordinate> currentCellReferences = getCurrentCellReferences(entry.getValue().getOriginalValue());

            for (Coordinate coordinate : currentCellReferences) {
                // Adding an inactive cell to the dependency graph
                if(!dependencyGraph.containsKey(coordinate)) {
                    dependencyGraph.put(coordinate, new LinkedList<>());
                } //TODO isn't this copying the list? we already got it ...

                // Get the list of cells that reference this coordinate
                List<Coordinate> referencesList = dependencyGraph.get(coordinate);

                // Add the current cell coordinate to the list of references
                referencesList.add(entry.getKey());
            }

            entry.getValue().setDependsOn(currentCellReferences); //TODO test
        }

        return dependencyGraph;
    }

    private List<Coordinate> getCurrentCellReferences(String cellOriginalValue) {
        String rangeName = ExpressionUtils.extractRange(cellOriginalValue);
        List<Coordinate> allCoordinates = new LinkedList<>();

        if (rangeName != null) {
            List<Coordinate> currentCellRangeCoordinates = getRange(rangeName);
            allCoordinates.addAll(currentCellRangeCoordinates);
        }

        List<Coordinate> currentCellReferences = ExpressionUtils.extractReferences(cellOriginalValue);

        if (!currentCellReferences.isEmpty()) {
            allCoordinates.addAll(currentCellReferences);
        }

        return allCoordinates;
    }

    // MUSHPA ME
    // This is the transposed graph of the dependency graph

    private void updateReferenceGraph() {
        Map<Coordinate, List<Coordinate>> referenceGraph = new HashMap<>();

        for (Map.Entry<Coordinate, List<Coordinate>> entry : cellInfluenceOn.entrySet()) {
            Coordinate dependent = entry.getKey();
            List<Coordinate> references = entry.getValue();

            for (Coordinate reference : references) {
                referenceGraph.computeIfAbsent(reference, k -> new LinkedList<>()).add(dependent);

            }
        }

        cellDependsOn = referenceGraph;
        updateCellsInfluenceOn();
    }
    private void updateCellsInfluenceOn() {
        for(Map.Entry<Coordinate, Cell> entry : activeCells.entrySet()) {
            List<Coordinate> influenceOnCoordinate = cellInfluenceOn.get(entry.getKey());

            if (influenceOnCoordinate != null) {
                entry.getValue().setInfluenceOn(influenceOnCoordinate);
            } else {
                entry.getValue().setInfluenceOn(new LinkedList<>());
            }
        }
    }

    // Using Topological Sort to get the right order
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
    public void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor) {
        Cell cell = addNewCellIfEmptyCell(cellToUpdateCoordinate);
        cell.setBackgroundColor(backgroundColor);
    }

    @Override
    public void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor) {
        Cell cell = addNewCellIfEmptyCell(cellToUpdateCoordinate);
        cell.setTextColor(textColor);
    }

    private void validateCoordinateInbound(Coordinate coordinate) {
        if(coordinate.getRow() > numberOfRows || coordinate.getRow() < 1 ||
                coordinate.getColumn() > numberOfColumns || coordinate.getColumn() < 1) {
            throw new InvalidCellBoundsException(coordinate, numberOfRows, numberOfColumns);
        }
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
        if (rowHeightUnits < 1) {
            throw new IllegalArgumentException("Row height units must be at least 1. Provided value: " + rowHeightUnits);
        }

        this.rowHeightUnits = rowHeightUnits;
    }

    @Override
    public void setColumnWidthUnits(int columnWidthUnits) {
        if (columnWidthUnits < 1) {
            throw new IllegalArgumentException("Column width units must be at least 1. Provided value: " + columnWidthUnits);
        }

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
    public Map<Coordinate, List<Coordinate>> getCellInfluenceOn() {
        return cellInfluenceOn;
    }

    @Override
    public Map<Coordinate, List<Coordinate>> getCellDependsOn() {
        return cellDependsOn;
    }

    @Override
    public int getVersionNumber() {
        return versionNumber - 1;
    }

    @Override
    public List<Cell> getLastModifiedCells() {
        return lastModifiedCells;
    }

    public SheetImpl copySheet() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SheetImpl) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Unknown error occurred while copying sheet");
        }
    }
}
