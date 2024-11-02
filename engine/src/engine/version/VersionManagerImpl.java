package engine.version;

import engine.sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;

public class VersionManagerImpl implements VersionManager {
    private int versionNumber;
    private final Map<Integer, Sheet> versions; // Version Number -> Sheet

    public VersionManagerImpl() {
        versionNumber = 0;
        versions = new HashMap<>();
    }

    @Override
    public void addNewVersion(Sheet sheet) {
        versionNumber = sheet.getVersionNumber();
        versions.put(versionNumber, sheet);
    }

    @Override
    public int getCurrentVersionNumber() {
        if(versions.isEmpty()) {
            throw new IllegalStateException("No versions available");
        }

        return versionNumber;
    }

    @Override
    public Sheet getSheetByVersionNumber(int requestedVersionNumber) {
        if(versions.isEmpty()) {
            throw new IllegalStateException("No versions available");
        }

        if(!versions.containsKey(requestedVersionNumber)) {
            throw new IllegalArgumentException("There is no sheet with version number " + requestedVersionNumber);
        }

        return versions.get(requestedVersionNumber);
    }

    @Override
    public Sheet getCurrentVersionSheet() {
        if(versions.isEmpty()) {
            throw new IllegalStateException("No versions available");
        }

        return versions.get(versionNumber);
    }
}
