package client.component.dashboard.sheetlist;

import client.component.dashboard.sheetlist.model.SingleSheetInformation;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import dto.SheetInfoDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableView;
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

    public SheetListRefresher(Consumer<List<SheetInfoDTO>> sheetsListConsumer) {
        this.sheetsListConsumer = sheetsListConsumer;
    }

    @Override
    public void run() {
        Consumer<String> responseHandler = (response) -> {
            SheetInfoDTO[] sheetsInfo = GSON_INSTANCE.fromJson(response, SheetInfoDTO[].class);
            if (sheetsInfo != null) {
                sheetsListConsumer.accept(Arrays.asList(sheetsInfo));
            }
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.SHEET_LIST_PATH, HttpMethod.GET, null, responseHandler);
    }
}