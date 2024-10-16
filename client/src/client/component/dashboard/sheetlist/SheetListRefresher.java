package client.component.dashboard.sheetlist;

import client.util.Constants;
import client.util.http.HttpClientUtil;
import dto.SheetInfoDTO;
import javafx.beans.property.SimpleIntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.util.Constants.GSON_INSTANCE;

public class SheetListRefresher extends TimerTask {

    private final Consumer<List<SheetInfoDTO>> sheetsListConsumer;
    private final SimpleIntegerProperty numberOfSheets;

    public SheetListRefresher(Consumer<List<SheetInfoDTO>> sheetsListConsumer, SimpleIntegerProperty numberOfSheets) {
        this.sheetsListConsumer = sheetsListConsumer;
        this.numberOfSheets = numberOfSheets;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(Constants.SHEET_LIST_PATH, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfSheets = response.body().string();
                SheetInfoDTO[] sheetsInfo = GSON_INSTANCE.fromJson(jsonArrayOfSheets, SheetInfoDTO[].class);
                if (sheetsInfo != null && numberOfSheets.get() != sheetsInfo.length) {
                    sheetsListConsumer.accept(Arrays.asList(sheetsInfo));
                }
            }
        });
    }
}