package client.util.http;

import dto.range.RangeDTO;
import dto.sheet.SheetDTO;
import dto.requestinfo.UpdateInformation;
import dto.coordinate.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface SheetService {
    void updateCell(UpdateInformation updateInformation, Consumer<SheetDTO> updateSheet);
    void updateCellBackgroundColor(UpdateInformation updateInformation, Runnable updateView);
    void updateCellTextColor(UpdateInformation updateInformation, Runnable updateView);
    void getSheetByVersion(int requestedVersionNumber, Consumer<SheetDTO> displaySheet);
    void addRange(String rangeName, String rangeCoordinates, int version, Consumer<RangeDTO> updateRanges);
    void deleteRange(String rangeNameToDelete, int version, Runnable deleteRange);
    void getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy, Consumer<SheetDTO> displaySheet);
    void getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues, Consumer<SheetDTO> displayFiltered);
    void getExpectedValue(UpdateInformation updateInformation, Consumer<SheetDTO> updateValue);
    void getAxis(String axisRange, Consumer<List<Coordinate>> listConsumer);
}
