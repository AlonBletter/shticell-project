package client.util.http;

import dto.RangeDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface SheetService {
    void updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue, Consumer<SheetDTO> updateSheet);
    void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor, Runnable updateView);
    void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor, Runnable updateView);
    void getSheetByVersion(int requestedVersionNumber, Consumer<SheetDTO> displaySheet);
    void addRange(String rangeName, String rangeCoordinates, Consumer<RangeDTO> updateRanges);
    void deleteRange(String rangeNameToDelete, Runnable deleteRange);
    void getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy, Consumer<SheetDTO> displaySheet);
    void getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues, Consumer<SheetDTO> displayFiltered);
    void getExpectedValue(Coordinate cellToCalculate, String newValueOfCell, Consumer<SheetDTO> updateValue);
    void getAxis(String axisRange, Consumer<List<Coordinate>> listConsumer);
}
