package client.component.sheet.header;

import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.TimerTask;
import java.util.function.Consumer;

public class VersionRefresher extends TimerTask {
    private SimpleIntegerProperty currentVersion;
    private Runnable updateUserOnNewVersion;
    private SimpleBooleanProperty shouldRefresh;

    public VersionRefresher(SimpleIntegerProperty currentVersion, Runnable updateUserOnNewVersion, SimpleBooleanProperty shouldRefresh) {
        this.currentVersion = currentVersion;
        this.updateUserOnNewVersion = updateUserOnNewVersion;
        this.shouldRefresh = shouldRefresh;
    }

    @Override
    public void run() {
        if(!shouldRefresh.get()) {
            return;
        }

        Consumer<String> responseHandler = (response) -> {
            if(response != null) {
                int version = JsonParser.parseString(response).getAsJsonObject().get("version").getAsInt();

                if(version != currentVersion.getValue()) {
                    updateUserOnNewVersion.run();
                }
            }
        };

        HttpClientUtil.runReqSyncWithJson(Constants.GET_VERSION_OF_SHEET_PATH, HttpMethod.GET, null, responseHandler);
    }
}
