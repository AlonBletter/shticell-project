package client.util.http;

import client.util.Constants;
import dto.CellDTO;
import dto.SheetDTO;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.range.Range;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static client.util.Constants.ADAPTED_GSON;

public class SheetServiceImpl implements SheetService {
    @Override
    public SheetDTO getSpreadsheet() {
        String finalUrl = HttpUrl
                .parse(Constants.GET_SHEET_PATH)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() != 200) {

                } else {
                    SheetDTO requestedSheet = ADAPTED_GSON.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {

                    });
                }
            }
        });
        return null; //TODO fix
    }

    @Override
    public CellDTO getCell(Coordinate cellToGetCoordinate) {
        return null;
    }

    @Override
    public void updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue) {

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
    public SheetDTO getSheetByVersion(int requestedVersionNumber) {
        return null;
    }

    @Override
    public void writeSystemDataToFile(String filePath) throws IOException {

    }

    @Override
    public void readSystemDataFromFile(String filePath) throws IOException, ClassNotFoundException {

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
