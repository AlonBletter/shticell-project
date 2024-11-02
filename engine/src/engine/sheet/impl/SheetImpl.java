package engine.sheet.impl;

import engine.exception.InvalidCellBoundsException;
import engine.expression.ExpressionUtils;
import engine.generated.*;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.CellReadActions;
import engine.sheet.cell.impl.CellImpl;
import dto.coordinate.Coordinate;
import dto.coordinate.CoordinateFactory;
import dto.effectivevalue.EffectiveValue;
import engine.sheet.range.Range;
import engine.sheet.range.RangeImpl;
import engine.sheet.row.SortableRow;

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
    private final Map<String, Range> ranges;
    private final Map<String, Set<Coordinate>> activeRanges;
    private int versionNumber;

    public SheetImpl() {
        activeCells = new HashMap<>();
        cellInfluenceOn = new HashMap<>();
        cellDependsOn = new HashMap<>();
        lastModifiedCells = new LinkedList<>();
        ranges = new HashMap<>();
        activeRanges = new HashMap<>();
        versionNumber = 1;
    }

    @Override
    public CellReadActions getCell(Coordinate coordinate) {
        validateCoordinateInbound(coordinate);

        Cell cell = activeCells.get(coordinate);

        if(cell == null) {
            activeCells.computeIfAbsent(coordinate, CellImpl::new);
            return activeCells.get(coordinate);
        }

        return cell;
    }

     private Cell addNewCellIfEmptyCell(Coordinate coordinate) {
        validateCoordinateInbound(coordinate);

         return activeCells.computeIfAbsent(coordinate, CellImpl::new);
     }

    @Override
    public boolean updateCell(Coordinate coordinate, String newOriginalValue, String modifiedBy) {
        Cell cellToUpdate = addNewCellIfEmptyCell(coordinate);

        lastModifiedCells.clear();
        cellToUpdate.setOriginalValue(newOriginalValue);
        updateSheetEffectiveValues();

        if(!lastModifiedCells.isEmpty()) {
            versionNumber++;

            for(Cell cell : lastModifiedCells) {
                cell.setLastModifiedBy(modifiedBy);
            }
        }

        return !lastModifiedCells.isEmpty();
    }

    @Override
    public void addRange(String rangeName, String rangeCoordinates) {
        List<Coordinate> startEnd = ExpressionUtils.parseRange(rangeCoordinates);
        Coordinate startOfRange = startEnd.get(0);
        Coordinate endOfRange = startEnd.get(1);
        addRangeHelper(rangeName, startOfRange, endOfRange);
    }

    private void addRangeHelper(String rangeName, Coordinate startOfRange, Coordinate endOfRange) {
        try {
            validateCoordinateInbound(startOfRange);
            validateCoordinateInbound(endOfRange);
        } catch (InvalidCellBoundsException e) {
            throw new InvalidCellBoundsException(e.getActualCoordinate(), e.getSheetNumOfRows(), e.getSheetNumOfColumns(),
                    "Range [" + rangeName + "] coordinates are invalid.");
        }

        if(ranges.containsKey(rangeName)) {
            throw new IllegalArgumentException("Range with the name [" + rangeName + "] already exists.");
        } else {
            ranges.put(rangeName, new RangeImpl(rangeName, startOfRange, endOfRange));
        }
    }

    @Override
    public void deleteRange(String rangeNameToDelete) {
        if(!ranges.containsKey(rangeNameToDelete)) {
            throw new IllegalArgumentException("Range with the name [" + rangeNameToDelete + "] does not exist.");
        }

        if(activeRanges.containsKey(rangeNameToDelete)) {
            throw new IllegalArgumentException("Range [" + rangeNameToDelete + "] cannot be deleted " +
                    "because it's being used by " + activeRanges.get(rangeNameToDelete));
        }

        ranges.remove(rangeNameToDelete);
        updateSheetEffectiveValues();
    }

    @Override
    public List<Coordinate> getRangeCellsCoordinates(String rangeNameToView) {
        Range range = ranges.get(rangeNameToView);

        if (range == null) {
            return null;
        }

        return range.getCellsInRange();
    }

    @Override
    public List<Range> getRanges() {
        return new ArrayList<>(ranges.values());
    }

    @Override
    public Range getRange(String rangeNameToView) {
        Range range = ranges.get(rangeNameToView);

        if(range == null) {
            throw new IllegalArgumentException("Range with the name [" + rangeNameToView + "] does not exist.");
        }

        return range;
    }

    @Override
    public void init(String uploadedBy, STLSheet sheetToInitFrom) {
        setName(sheetToInitFrom.getName());
        setNumberOfRows(sheetToInitFrom.getSTLLayout().getRows());
        setNumberOfColumns(sheetToInitFrom.getSTLLayout().getColumns());
        setRowHeightUnits(sheetToInitFrom.getSTLLayout().getSTLSize().getRowsHeightUnits());
        setColumnWidthUnits(sheetToInitFrom.getSTLLayout().getSTLSize().getColumnWidthUnits());

        STLCells cellsFromFile = sheetToInitFrom.getSTLCells();
        List<STLCell> cellsList = cellsFromFile.getSTLCell();
        STLRanges stlRanges = sheetToInitFrom.getSTLRanges();

        if(stlRanges != null) {
            List<STLRange> rangesList = stlRanges.getSTLRange();

            for (STLRange stlRange : rangesList) {
                STLBoundaries boundaries = stlRange.getSTLBoundaries();
                Coordinate from = CoordinateFactory.createCoordinate(boundaries.getFrom());
                Coordinate to = CoordinateFactory.createCoordinate(boundaries.getTo());
                addRangeHelper(stlRange.getName(), from, to);
            }
        }

        for (STLCell stlCell : cellsList) {
            Coordinate cellCoordinate = CoordinateFactory.createCoordinate(stlCell.getRow(), stlCell.getColumn());
            Cell cell = addNewCellIfEmptyCell(cellCoordinate);
            cell.setOriginalValue(stlCell.getSTLOriginalValue());
            cell.setLastModifiedBy(uploadedBy);
            activeCells.put(cellCoordinate, cell);
        }

        updateSheetEffectiveValues();
        versionNumber++;
    }

    private void updateSheetEffectiveValues() {
        activeRanges.clear();
        Map<Coordinate, List<Coordinate>> dependencyGraph = createDependencyGraph();
        List<Coordinate> effectiveValueCalculationOrder = getEffectiveValueCalculationOrder(dependencyGraph);

        for(Coordinate coordinate : effectiveValueCalculationOrder) {
            updateCellEffectiveValue(addNewCellIfEmptyCell(coordinate));
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

            Set<Coordinate> currentCellReferences = getCurrentCellDependencies(entry.getValue().getOriginalValue(), entry.getKey());

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

            entry.getValue().setDependsOn(new LinkedList<>(currentCellReferences));
        }

        return dependencyGraph;
    }

    private Set<Coordinate> getCurrentCellDependencies(String cellOriginalValue, Coordinate currentCell) {
        Set<String> extractRanges = ExpressionUtils.extractRanges(cellOriginalValue);
        Set<Coordinate> allCoordinates = new HashSet<>();

        for (String rangeName : extractRanges) {
            List<Coordinate> currentCellRangeCoordinates = getRangeCellsCoordinates(rangeName);
            if (rangeName != null && currentCellRangeCoordinates != null) {
                allCoordinates.addAll(currentCellRangeCoordinates);
                activeRanges.computeIfAbsent(rangeName, k -> new HashSet<>()).add(currentCell);
            }
        }

        Set<Coordinate> currentCellReferences = ExpressionUtils.extractReferences(cellOriginalValue);

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

            entry.getValue().setInfluenceOn(
                    Objects.requireNonNullElseGet(influenceOnCoordinate, LinkedList::new));
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
    public void sort(String rangeToSortBy, List<String> columnsToSortBy) {
        List<Coordinate> rangeStartEnd = ExpressionUtils.parseRange(rangeToSortBy);
        Coordinate start = rangeStartEnd.get(0);
        Coordinate end = rangeStartEnd.get(1);
        validateRangeCoordinates(start, end);
        List<Integer> columnIndicesToSort = getValidatedUniqueColumnIndices(columnsToSortBy, start, end);

        List<SortableRow> sortableRows = getSortableRows(start, end, columnIndicesToSort);
        sortableRows.sort(getSortableRowComparator(columnsToSortBy));
        updateRowsAfterModification(sortableRows, start);
    }

    private void validateRangeCoordinates(Coordinate start, Coordinate end) {
        validateCoordinateInbound(start);
        validateCoordinateInbound(end);

        int startRow = start.row();
        int startCol = start.column();
        int endRow = end.row();
        int endCol = end.column();

        if (endRow < startRow || endCol < startCol) {
            throw new IllegalArgumentException("Invalid range: The bottom-right coordinate [" + end + "]" +
                    " cannot be above or to the left of the top-left coordinate [" + start + "].");
        }
    }

    private List<Integer> getValidatedUniqueColumnIndices(List<String> columnsToSortBy, Coordinate start, Coordinate end) {
        Set<String> uniqueColumns = new HashSet<>();
        for (String column : columnsToSortBy) {
            if (!uniqueColumns.add(column)) {
                throw new IllegalArgumentException("Column [" + column + "] appears more than once.");
            }
        }

        List<Integer> columnIndicesToSort = new ArrayList<>();
        int startColumn = start.column();
        int endColumn = end.column();

        for (String column : columnsToSortBy) {
            int columnIndex = parseSingleLetterColumn(column);

            if (columnIndex < startColumn || columnIndex > endColumn) {
                String startColumnLetter = convertIntegerColumnToLetter(startColumn);
                String endColumnLetter = convertIntegerColumnToLetter(endColumn);

                throw new IllegalArgumentException("Invalid column selection.\n" +
                        "Got column [" + column + "] \n" +
                        "Expected a column between [" + startColumnLetter + "] and [" + endColumnLetter + "].");
            }

            columnIndicesToSort.add(columnIndex);
        }

        return columnIndicesToSort;
    }

    private List<SortableRow> getSortableRows(Coordinate start, Coordinate end, List<Integer> columnIndicesToSort) {
        List<SortableRow> sortableRows = new ArrayList<>();
        for (int row = start.row(); row <= end.row(); row++) {
            Map<Integer, Double> valuesToSort = new HashMap<>();
            List<Cell> cellsInRow = new ArrayList<>();
            int startColumn = start.column();
            int endColumn = end.column();

            for (int col = startColumn; col <= endColumn; col++) {
                Coordinate cellCoordinate = CoordinateFactory.createCoordinate(row, col);
                Cell cell = addNewCellIfEmptyCell(cellCoordinate);
                if (columnIndicesToSort.contains(col)) {
                    Double value = cell.getEffectiveValue().extractValueWithExpectation(Double.class);
                    valuesToSort.put(col, value);
                }

                cellsInRow.add(cell);
            }

            sortableRows.add(new SortableRow(row, startColumn, endColumn, valuesToSort, cellsInRow));
        }
        return sortableRows;
    }

    private void updateRowsAfterModification(List<SortableRow> sortableRows, Coordinate start) {
        for (int row = 0; row < sortableRows.size(); row++) {
            SortableRow sortedRow = sortableRows.get(row);
            List<Cell> sortedCells = sortedRow.getCellsInRow();
            int columnIndex = start.column();

            for (Cell cell : sortedCells) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(start.row() + row, columnIndex);
                activeCells.put(coordinate, cell);
                columnIndex++;
            }
        }
    }

    private Comparator<SortableRow> getSortableRowComparator(List<String> columnsToSortBy) {
        return (row1, row2) -> {
            for (String column : columnsToSortBy) {
                int colIndex = parseSingleLetterColumn(column);
                Double value1 = row1.getNumericValue(colIndex);
                Double value2 = row2.getNumericValue(colIndex);

                if (value1 == null && value2 != null) return 1;
                if (value1 != null && value2 == null) return -1;

                if (value1 != null && value2 != null) {
                    int comparison = value1.compareTo(value2);
                    if (comparison != 0) {
                        return comparison;
                    }
                }
            }

            return Integer.compare(row1.getOriginalRow(), row2.getOriginalRow());
        };
    }

    private int parseSingleLetterColumn(String column) {
        if (column.length() != 1 || !Character.isLetter(column.charAt(0))) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }

        char normalizedColumn = Character.toUpperCase(column.charAt(0));
        return normalizedColumn - 'A' + 1;
    }

    private String convertIntegerColumnToLetter(int column) {
        if (column < 1 || column > 26) {
            throw new IllegalArgumentException("Column must be between 1 and 26");
        }
        return String.valueOf((char) ('A' + column - 1));
    }

    @Override
    public void filter(String rangeToFilter, Map<String, List<String>> filterRequestValues) {
        List<Coordinate> rangeStartEnd = ExpressionUtils.parseRange(rangeToFilter);
        Coordinate start = rangeStartEnd.get(0);
        Coordinate end = rangeStartEnd.get(1);
        int numberOfRowsToFilter = end.row() - start.row() + 1;
        validateRangeCoordinates(start, end);
        List<Integer> filterColumnIndices = getValidatedUniqueColumnIndices(filterRequestValues.keySet().stream().toList(), start, end);
        List<SortableRow> sortableRows = getSortableRows(start, end, filterColumnIndices);

        List<SortableRow> filteredRows = new ArrayList<>();

        for (SortableRow row : sortableRows) {
            boolean allMatch = true;

            for (Map.Entry<String, List<String>> entry : filterRequestValues.entrySet()) {
                String column = entry.getKey();
                List<String> filterValues = entry.getValue();
                int columnIndex = parseSingleLetterColumn(column);
                String effectiveValueString = row.getEffectiveValueString(columnIndex);
                String value = !effectiveValueString.isEmpty() ? effectiveValueString : "(Empty Cell/s)";
                if (!filterValues.contains(value)) {
                    allMatch = false;
                    break;
                }
            }

            if (allMatch) {
                filteredRows.add(row);
            }
        }

        updateFilteredRows(filteredRows, start, end, numberOfRowsToFilter);
    }

    private void updateFilteredRows(List<SortableRow> filteredRows, Coordinate start, Coordinate end, int numberOfRowsToFilter) {
        for (int row = 0; row < numberOfRowsToFilter; row++) {
            for (int column = start.column(); column <= end.column(); column++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(start.row() + row, column);
                activeCells.remove(coordinate);
            }
        }

        updateRowsAfterModification(filteredRows, start);

        for (int row = filteredRows.size(); row < numberOfRowsToFilter; row++) {
            for (int column = start.column(); column <= end.column(); column++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(start.row() + row, column);
                activeCells.put(coordinate, addNewCellIfEmptyCell(coordinate));
            }
        }
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
        if(coordinate.row() > numberOfRows || coordinate.row() < 1 ||
                coordinate.column() > numberOfColumns || coordinate.column() < 1) {
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