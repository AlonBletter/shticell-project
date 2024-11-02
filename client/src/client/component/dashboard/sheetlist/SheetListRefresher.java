package client.component.dashboard.sheetlist;

import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import dto.requestinfo.SheetInfoDTO;

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

        HttpClientUtil.runReqSyncWithJson(Constants.SHEET_LIST_PATH, HttpMethod.GET, null, responseHandler);
    }
}