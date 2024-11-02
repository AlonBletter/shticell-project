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

    public static OkHttpClient getClient() {
        return HTTP_CLIENT;
    }

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runReqAsyncWithJson(String finalUrl, HttpMethod methodType, RequestBody requestBody, Consumer<String> responseConsumer) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(finalUrl);

        getRequestType(methodType, requestBody, requestBuilder);

        Request request = requestBuilder.build();

        sendAsyncRequest(responseConsumer, request);
    }

    private static void getRequestType(HttpMethod methodType, RequestBody requestBody, Request.Builder requestBuilder) {
        switch (methodType) {
            case POST:
                requestBuilder.post(requestBody);
                break;
            case PUT:
                requestBuilder.put(requestBody);
                break;
            case DELETE:
                requestBuilder.delete(requestBody);
                break;
            case PATCH:
                requestBuilder.patch(requestBody);
                break;
            case GET:
                requestBuilder.get();
                break;
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + methodType);
        }
    }

    private static void sendAsyncRequest(Consumer<String> responseConsumer, Request request) {
        Callback callback = new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    handleResponse(responseConsumer, response);
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handleFailure(e);
            }
        };

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    private static void handleFailure(IOException e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("HTTP Request Error");
            alert.setHeaderText("An error occurred during the HTTP request.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }

    public static void runReqSyncWithJson(String finalUrl, HttpMethod methodType, RequestBody requestBody, Consumer<String> responseConsumer) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(finalUrl);

        getRequestType(methodType, requestBody, requestBuilder);

        Request request = requestBuilder.build();

        runSyncRequest(responseConsumer, request);
    }

    private static void runSyncRequest(Consumer<String> responseConsumer, Request request) {
        try {
            Call call = HTTP_CLIENT.newCall(request);
            Response response = call.execute();

            handleResponse(responseConsumer, response);
        } catch (IOException e) {
            handleFailure(e);
        }
    }

    private static void handleResponse(Consumer<String> responseConsumer, Response response) throws IOException {
        if (response.code() == 204) {
            responseConsumer.accept(null);
        } else {
            String json = response.body().string();
            if (response.isSuccessful() && json != null) {
                responseConsumer.accept(json);
            } else {
                JsonObject errorObject = JsonParser.parseString(json).getAsJsonObject();
                String errorMessage = errorObject.get("error").getAsString();
                handleFailure(new IOException(errorMessage));
            }
        }
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
