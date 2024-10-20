package client.util.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    // TODO consumer<>
    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    //TODO testing
    public static void runGetAsyncWithJson(String finalUrl, Consumer<String> responseConsumer) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        processTheRequest(responseConsumer, request);
    }

    public static void runPostAsyncWithJson(String finalUrl, RequestBody requestBody, Consumer<String> responseConsumer) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        processTheRequest(responseConsumer, request);
    }

    private static void processTheRequest(Consumer<String> responseConsumer, Request request) {
        Callback callback = new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String json = response.body().string();
                    if (response.isSuccessful() && response.body() != null) {
                        responseConsumer.accept(json);
                    } else {
                        JsonObject errorObject = JsonParser.parseString(json).getAsJsonObject();
                        String errorMessage = errorObject.get("error").getAsString();
                        handleFailure(new IOException(errorMessage));
                    }
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handleFailure(e);
            }

            private void handleFailure(IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("HTTP Request Error");
                    alert.setHeaderText("An error occurred during the HTTP request.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        };

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsyncPost(String finalUrl, RequestBody requestBody, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
