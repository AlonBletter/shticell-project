package engine.version;

import engine.sheet.api.Sheet;

public interface VersionManager {
    void addNewVersion(Sheet sheet);
    int getCurrentVersionNumber();
    Sheet getCurrentVersionSheet();
    Sheet getSheetByVersionNumber(int requestedVersionNumber);
}
