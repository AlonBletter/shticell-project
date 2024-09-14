package gui.task;

import javafx.concurrent.Task;

public class LoadFileTask extends Task<Boolean> {
    private String filePathToLoad;

    public LoadFileTask(String filePathToLoad) {
        this.filePathToLoad = filePathToLoad;
    }

    @Override
    protected Boolean call() throws Exception {



        return null;
    }
}
