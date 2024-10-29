package client.util.http;

import client.util.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dto.requestinfo.UpdateInformation;
import dto.requestinfo.FilterParams;
import dto.range.RangeDTO;
import dto.sheet.SheetDTO;
import dto.coordinate.Coordinate;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static client.util.Constants.GSON_INSTANCE;

public class SheetServiceImpl implements SheetService {
    @Override
    public void updateCell(UpdateInformation updateInformation, Consumer<SheetDTO> updateSheet) {
        String jsonObject = GSON_INSTANCE.toJson(updateInformation);

        RequestBody requestBody = RequestBody.create(
                jsonObject,
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                SheetDTO sheetDTO = GSON_INSTANCE.fromJson(response, SheetDTO.class);
                Platform.runLater(() -> updateSheet.accept(sheetDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.UPDATE_CELL_PATH, HttpMethod.PUT, requestBody, responseHandler);
    }

    @Override
    public void updateCellBackgroundColor(UpdateInformation updateInformation, Runnable updateView) {
        String jsonObject = GSON_INSTANCE.toJson(updateInformation);

        RequestBody requestBody = RequestBody.create(
                jsonObject,
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                Platform.runLater(updateView);
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.UPDATE_CELL_BACKGROUND_COLOR_PATH, HttpMethod.PUT, requestBody, responseHandler);
    }

    @Override
    public void updateCellTextColor(UpdateInformation updateInformation, Runnable updateView) {
        String jsonObject = GSON_INSTANCE.toJson(updateInformation);

        RequestBody requestBody = RequestBody.create(
                jsonObject,
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                Platform.runLater(updateView);
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.UPDATE_CELL_TEXT_COLOR_PATH, HttpMethod.PUT, requestBody, responseHandler);
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
                SheetDTO sheetDTO = GSON_INSTANCE.fromJson(response, SheetDTO.class);
                Platform.runLater(() -> displaySheet.accept(sheetDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(finalUrl, HttpMethod.GET, null, responseHandler);
    }

    @Override
    public void addRange(String rangeName, String rangeCoordinates, int version, Consumer<RangeDTO> updateRanges) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rangeName", rangeName);
        jsonObject.addProperty("coordinates", rangeCoordinates);
        jsonObject.addProperty("version", version);

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                RangeDTO rangeDTO = GSON_INSTANCE.fromJson(response, RangeDTO.class);
                Platform.runLater(() -> updateRanges.accept(rangeDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.ADD_RANGE_PATH, HttpMethod.POST, requestBody, responseHandler);
    }

    @Override
    public void deleteRange(String rangeNameToDelete, int version, Runnable deleteRange) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rangeName", rangeNameToDelete);
        jsonObject.addProperty("version", version);

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                Platform.runLater(deleteRange);
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.DELETE_RANGE_PATH, HttpMethod.DELETE, requestBody, responseHandler);
    }

    @Override
    public void getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy, Consumer<SheetDTO> displaySheet) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("range", rangeToSortBy);
        JsonArray columnsArray = new JsonArray();
        for (String column : columnsToSortBy) {
            columnsArray.add(column);
        }

        jsonObject.add("columns", columnsArray);

        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                SheetDTO sheetDTO = GSON_INSTANCE.fromJson(response, SheetDTO.class);
                Platform.runLater(() -> displaySheet.accept(sheetDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.GET_SORTED_SHEET_PATH, HttpMethod.POST, requestBody, responseHandler);
    }

    @Override
    public void getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues, Consumer<SheetDTO> displayFiltered) {
        FilterParams filterParams = new FilterParams(rangeToFilter, filterRequestValues);
        String jsonObject = GSON_INSTANCE.toJson(filterParams);

        RequestBody requestBody = RequestBody.create(
                jsonObject,
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                SheetDTO sheetDTO = GSON_INSTANCE.fromJson(response, SheetDTO.class);
                Platform.runLater(() -> displayFiltered.accept(sheetDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.GET_FILTERED_SHEET_PATH, HttpMethod.POST, requestBody, responseHandler);
    }

    @Override
    public void getExpectedValue(UpdateInformation updateInformation, Consumer<SheetDTO> updateView) {
        String jsonObject = GSON_INSTANCE.toJson(updateInformation);

        RequestBody requestBody = RequestBody.create(
                jsonObject,
                okhttp3.MediaType.parse("application/json")
        );

        Consumer<String> responseHandler = response -> {
            if (response != null) {
                SheetDTO sheetDTO = GSON_INSTANCE.fromJson(response, SheetDTO.class);
                Platform.runLater(() -> updateView.accept(sheetDTO));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.GET_EXPECTED_VALUE_PATH, HttpMethod.POST, requestBody, responseHandler);
    }

    @Override
    public void getAxis(String axisRange, Consumer<List<Coordinate>> listConsumer) {
        String finalUrl = HttpUrl
                .parse(Constants.GET_AXIS_PATH)
                .newBuilder()
                .addQueryParameter("axisRange", axisRange)
                .build()
                .toString();


        Consumer<String> responseHandler = response -> {
            if (response != null) {
                List<Coordinate> coordinates = GSON_INSTANCE.fromJson(response, new TypeToken<List<Coordinate>>() {}.getType());
                Platform.runLater(() -> listConsumer.accept(coordinates));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(finalUrl, HttpMethod.GET, null, responseHandler);
    }
}
