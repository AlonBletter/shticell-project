package client.component.dashboard.chat.chatarea;

import client.component.dashboard.chat.chatarea.model.ChatLinesWithVersion;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import okhttp3.HttpUrl;

import java.util.TimerTask;
import java.util.function.Consumer;

import static client.util.Constants.CHAT_LINES_LIST;
import static client.util.Constants.GSON_INSTANCE;


public class ChatAreaRefresher extends TimerTask {

    private final Consumer<ChatLinesWithVersion> chatlinesConsumer;
    private final IntegerProperty chatVersion;
    private final BooleanProperty shouldUpdate;

    public ChatAreaRefresher(IntegerProperty chatVersion, BooleanProperty shouldUpdate, Consumer<ChatLinesWithVersion> chatlinesConsumer) {
        this.chatlinesConsumer = chatlinesConsumer;
        this.chatVersion = chatVersion;
        this.shouldUpdate = shouldUpdate;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(CHAT_LINES_LIST)
                .newBuilder()
                .addQueryParameter("chatversion", String.valueOf(chatVersion.get()))
                .build()
                .toString();

        Consumer<String> responseHandler = (response) -> {
            ChatLinesWithVersion chatLinesWithVersion = GSON_INSTANCE.fromJson(response, ChatLinesWithVersion.class);
            chatlinesConsumer.accept(chatLinesWithVersion);
        };

        HttpClientUtil.runReqSyncWithJson(finalUrl, HttpMethod.GET, null, responseHandler);
    }

}
