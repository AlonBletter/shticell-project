package client.component.dashboard.chat.users;

import client.util.Constants;
import client.util.http.HttpClientUtil;
import client.util.http.HttpMethod;
import javafx.beans.property.BooleanProperty;

import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.util.Constants.GSON_INSTANCE;

public class UserListRefresher extends TimerTask {

    private final Consumer<List<String>> usersListConsumer;
    private final BooleanProperty shouldUpdate;


    public UserListRefresher(BooleanProperty shouldUpdate, Consumer<List<String>> usersListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.usersListConsumer = usersListConsumer;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        Consumer<String> responseHandler = (response) -> {
            String[] usersNames = GSON_INSTANCE.fromJson(response, String[].class);
            usersListConsumer.accept(Arrays.asList(usersNames));
        };

        HttpClientUtil.runReqAsyncWithJson(Constants.USERS_LIST, HttpMethod.GET, null, responseHandler);
    }
}
