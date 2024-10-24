package client.util.http;

import client.component.sheet.app.SheetController;
import client.util.Constants;
import com.google.gson.JsonObject;
import dto.CellDTO;
import dto.CoordinateAndValue;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static client.util.Constants.ADAPTED_GSON;
import static client.util.Constants.GSON_INSTANCE;

public class SheetServiceImpl implements SheetService {
    private SheetController sheetController;

    public SheetServiceImpl(SheetController sheetController) {
        this.sheetController = sheetController;
    }

    @Override
    public SheetDTO getSpreadsheet() {
        return null;
    }

    @Override
    public CellDTO getCell(Coordinate cellToGetCoordinate) {
        return null;
    }

    @Override
    public void updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue, Consumer<SheetDTO> updateSheet) {

        CoordinateAndValue coordinateAndValue = new CoordinateAndValue(cellToUpdateCoordinate, newCellOriginalValue);
        String jsonObject = ADAPTED_GSON.toJson(coordinateAndValue);

        RequestBody requestBody = RequestBody.create(
                jsonObject,
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                SheetDTO sheetDTO = ADAPTED_GSON.fromJson(response, SheetDTO.class);
                Platform.runLater(() -> updateSheet.accept(sheetDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.UPDATE_CELL_PATH, HttpMethod.PUT, requestBody, responseHandler);
    }

    @Override
    public void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor) {

    }

    @Override
    public void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor) {

    }

    @Override
    public int getCurrentVersionNumber() {
        return 0;
    }

    @Override
    public void getSheetByVersion(int requestedVersionNumber, Consumer<SheetDTO> displaySheet) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_SHEET_BY_VERSION_PATH)
                .newBuilder()
                .addQueryParameter("version", String.valueOf(requestedVersionNumber))
                .build()
                .toString();


        Consumer<String> responseHandler = response -> {
            if (response != null) {
                SheetDTO sheetDTO = ADAPTED_GSON.fromJson(response, SheetDTO.class);
                Platform.runLater(() -> displaySheet.accept(sheetDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(finalUrl, HttpMethod.GET, null, responseHandler);
    }

    @Override
    public void addRange(String rangeName, String rangeCoordinates) {

    }

    @Override
    public void deleteRange(String rangeNameToDelete) {

    }

    @Override
    public List<Coordinate> getRange(String rangeNameToView) {
        return List.of();
    }

    @Override
    public List<Range> getRanges() {
        return List.of();
    }

    @Override
    public SheetDTO getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy) {
        return null;
    }

    @Override
    public List<String> getColumnUniqueValue(String columnLetter) {
        return List.of();
    }

    @Override
    public SheetDTO getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues) {
        return null;
    }

    @Override
    public SheetDTO getExpectedValue(Coordinate cellToCalculate, String newValueOfCell) {
        return null;
    }

    @Override
    public List<Coordinate> getAxis(String axisRange) {
        return List.of();
    }
}
