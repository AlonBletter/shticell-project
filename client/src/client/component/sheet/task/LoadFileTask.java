package client.component.sheet.task;

import engine.SheetManager;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class LoadFileTask extends Task<Boolean> {
    private final SheetManager engine;
    private final String filePathToLoad;
    private final Consumer<Void> onSuccess;
    private final Consumer<Exception> onFailure;
    private static final int SLEEP_TIME = 200;

    public LoadFileTask(SheetManager engine, String filePathToLoad, Consumer<Void> onSuccess, Consumer<Exception> onFailure) {
        this.engine = engine;
        this.filePathToLoad = filePathToLoad;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    @Override
    protected Boolean call() {
        try {
            updateMessage("Fetching file...");
            updateProgress(0, 1);
            Thread.sleep(SLEEP_TIME);

            updateMessage("Analyzing data...");
            updateProgress(0.2, 1);
            Thread.sleep(SLEEP_TIME);

            updateMessage("Loading the sheet...");
            updateProgress(0.5, 1);
            Thread.sleep(SLEEP_TIME);
            engine.loadSystemSettingsFromFile(filePathToLoad);
            updateMessage("Restoring cells information...");
            updateProgress(0.8, 1);
            Thread.sleep(SLEEP_TIME);
            Platform.runLater(
                    () -> onSuccess.accept(null)
            );

            updateMessage("Done...");
            updateProgress(1, 1);
        } catch (Exception e) {
            updateMessage("Failed to load file.");

            Platform.runLater(
                    () -> onFailure.accept(e)
            );
        }

        return Boolean.TRUE;
    }
}