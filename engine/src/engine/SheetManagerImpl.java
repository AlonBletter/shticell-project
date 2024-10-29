package engine;

import dto.cell.CellDTO;
import dto.range.RangeDTO;
import dto.sheet.SheetDTO;
import dto.converter.CellConverter;
import dto.converter.SheetConverter;
import engine.exception.InvalidCellBoundsException;
import engine.expression.ExpressionUtils;
import engine.generated.STLSheet;
import engine.sheet.api.Sheet;
import engine.sheet.api.SheetReadActions;
import dto.coordinate.Coordinate;
import engine.sheet.impl.SheetImpl;
import engine.sheet.range.Range;
import engine.sheet.range.RangeImpl;
import engine.version.VersionManager;
import engine.version.VersionManagerImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.util.List;
import java.util.Map;

public class SheetManagerImpl implements SheetManager {
    public static final int SHEET_MAX_COLUMNS = 20;
    public static final int SHEET_MAX_ROWS = 50;

    private final VersionManager versionManager = new VersionManagerImpl();
    private Sheet whatIfCopy;

    @Override
    public void loadSystemSettingsFromFile(String filePath) {
        try {
            validateXMLFile(filePath);
            STLSheet sheetFromFile = readSheetFromXMLFile(filePath);
            validateXMLSheetLayout(sheetFromFile);
            Sheet loadedSheet = convertXMLSheetToMySheetObject(null, sheetFromFile); // old version
            versionManager.addNewVersion(loadedSheet);
        } catch (InvalidCellBoundsException e) {
            String message = e.getMessage() != null ? e.getMessage() : "";
            throw new InvalidCellBoundsException(e.getActualCoordinate(), e.getSheetNumOfRows(), e.getSheetNumOfColumns(), "Error while loading file: " + message);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new IllegalArgumentException("Error while loading file: " + e.getMessage());
        }
    }

    @Override
    public void loadSystemSettingsFromFile(String uploadedBy, InputStream fileInputStream) {
        try {
            STLSheet sheetFromFile = readSheetFromXMLFile(fileInputStream);
            validateXMLSheetLayout(sheetFromFile);
            Sheet loadedSheet = convertXMLSheetToMySheetObject(uploadedBy, sheetFromFile);
            versionManager.addNewVersion(loadedSheet);
        } catch (InvalidCellBoundsException e) {
            String message = e.getMessage() != null ? e.getMessage() : "";
            throw new InvalidCellBoundsException(e.getActualCoordinate(), e.getSheetNumOfRows(), e.getSheetNumOfColumns(), "Error while loading file: " + message);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new IllegalArgumentException("Error while loading file: " + e.getMessage());
        }
    }

    private STLSheet readSheetFromXMLFile(String filePath) {
        try {
            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (STLSheet) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to read the sheet from XML file", e);
        }
    }

    private STLSheet readSheetFromXMLFile(InputStream fileInputStream) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (STLSheet) jaxbUnmarshaller.unmarshal(fileInputStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to read the sheet from XML file", e);
        }
    }

    @Override
    public SheetDTO getSpreadsheet() {
        validateLoadedSheet();
        return SheetConverter.convertToDTO(versionManager.getCurrentVersionSheet());
    }

    private void validateLoadedSheet() {
        if(versionManager.getCurrentVersionSheet() == null) {
            throw new IllegalStateException("Sheet isn't loaded yet");
        }
    }

    @Override
    public CellDTO getCell(Coordinate cellToGetCoordinate) {
        validateLoadedSheet();
        Sheet currentVersion = versionManager.getCurrentVersionSheet();
        return CellConverter.convertToDTO(currentVersion.getCell(cellToGetCoordinate));
    }

    @Override
    public SheetDTO updateCell(String modifiedBy, Coordinate cellToUpdateCoordinate, String newCellOriginalValue, int sheetVersionFromUser) {
        validateLoadedSheet();
        validateVersion(sheetVersionFromUser);
        Sheet copyOfCurrentVersion = versionManager.getCurrentVersionSheet().copySheet();

        boolean updated = copyOfCurrentVersion.updateCell(cellToUpdateCoordinate, newCellOriginalValue, modifiedBy);

        if (updated) {
            versionManager.addNewVersion(copyOfCurrentVersion);
            return SheetConverter.convertToDTO(copyOfCurrentVersion);
        }

        return null;
    }

    @Override
    public SheetReadActions getSheetReadActions() {
        validateLoadedSheet();
        return versionManager.getCurrentVersionSheet();
    }

    @Override
    public int getCurrentVersionNumber() {
        validateLoadedSheet();
        return versionManager.getCurrentVersionNumber();
    }

    @Override
    public SheetDTO getSheetByVersion(int requestedVersionNumber) {
        validateLoadedSheet();
        return SheetConverter.convertToDTO(versionManager.getSheetByVersionNumber(requestedVersionNumber));
    }

    @Override
    public void updateCellBackgroundColor(Coordinate cellToUpdateCoordinate, String backgroundColor, int sheetVersionFromUser) {
        validateLoadedSheet();
        validateVersion(sheetVersionFromUser);
        Sheet currentVersion = versionManager.getCurrentVersionSheet();
        currentVersion.updateCellBackgroundColor(cellToUpdateCoordinate, backgroundColor);
    }

    @Override
    public void updateCellTextColor(Coordinate cellToUpdateCoordinate, String textColor, int sheetVersionFromUser) {
        validateLoadedSheet();
        validateVersion(sheetVersionFromUser);
        Sheet currentVersion = versionManager.getCurrentVersionSheet();
        currentVersion.updateCellTextColor(cellToUpdateCoordinate, textColor);
    }

    @Override
    public void addRange(String rangeName, String rangeCoordinates, int sheetVersionFromUser) {
        validateLoadedSheet();
        validateVersion(sheetVersionFromUser);

        Sheet currentVersion = versionManager.getCurrentVersionSheet();

        currentVersion.addRange(rangeName, rangeCoordinates);
    }

    private void validateVersion(int version) {
        int currentSheetVersion = versionManager.getCurrentVersionNumber();

        if(currentSheetVersion != version) {
            throw new IllegalStateException("Sheet version [" + version + "] does not match latest version number [" + currentSheetVersion + "]\n" +
                    "If you'd like to update, please load the latest version first.");
        }
    }

    @Override
    public void deleteRange(String rangeNameToDelete, int sheetVersionFromUser) {
        validateLoadedSheet();
        validateVersion(sheetVersionFromUser);
        Sheet currentVersion = versionManager.getCurrentVersionSheet();
        currentVersion.deleteRange(rangeNameToDelete);
    }

    @Override
    public RangeDTO getRange(String rangeNameToView) {
        validateLoadedSheet();
        Sheet currentVersion = versionManager.getCurrentVersionSheet();
        return RangeDTO.convertToRangeDTO(currentVersion.getRange(rangeNameToView));
    }

    @Override
    public List<Range> getRanges() {
        validateLoadedSheet();
        Sheet currentVersion = versionManager.getCurrentVersionSheet();
        return currentVersion.getRanges();
    }

    @Override
    public SheetDTO getSortedSheet(String rangeToSortBy, List<String> columnsToSortBy) {
        validateLoadedSheet();
        Sheet copyCurrentVersionSheet = versionManager.getCurrentVersionSheet().copySheet();
        copyCurrentVersionSheet.sort(rangeToSortBy, columnsToSortBy);
        return SheetConverter.convertToDTO(copyCurrentVersionSheet);
    }

    @Override
    public SheetDTO getFilteredSheet(String rangeToFilter, Map<String, List<String>> filterRequestValues) {
        validateLoadedSheet();
        Sheet copyCurrentVersionSheet = versionManager.getCurrentVersionSheet().copySheet();
        copyCurrentVersionSheet.filter(rangeToFilter, filterRequestValues);
        return SheetConverter.convertToDTO(copyCurrentVersionSheet);
    }

    @Override
    public SheetDTO getExpectedValue(Coordinate cellToCalculate, String newValueOfCell, int sheetVersionFromUser) {
        validateLoadedSheet();
        validateVersion(sheetVersionFromUser);
        if (whatIfCopy == null || whatIfCopy.getVersionNumber() != versionManager.getCurrentVersionNumber()) {
            whatIfCopy = versionManager.getCurrentVersionSheet().copySheet();
        }
        whatIfCopy.updateCell(cellToCalculate, newValueOfCell, null);

        return SheetConverter.convertToDTO(whatIfCopy);
    }

    @Override
    public List<Coordinate> getAxis(String axisRange) {
        validateLoadedSheet();
        List<Coordinate> axis = ExpressionUtils.parseRange(axisRange);
        Range range = new RangeImpl("axis", axis.getFirst(), axis.getLast());
        return range.getCellsInRange();
    }

    private void validateXMLFile(String filePath) {
        if(filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        } else if (filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        } else if (!filePath.toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException("File path must end with .xml");
        }
    }

    private void validateXMLSheetLayout(STLSheet sheetFromFile) {
        if(sheetFromFile.getSTLLayout() == null) {
            throw new IllegalArgumentException("Cannot load sheet without a specified layout");
        }

        int xmlSheetNumOfRows = sheetFromFile.getSTLLayout().getRows();
        int xmlSheetNumOfColumns = sheetFromFile.getSTLLayout().getColumns();

        if(xmlSheetNumOfRows > SHEET_MAX_ROWS || xmlSheetNumOfRows < 1 ||
                xmlSheetNumOfColumns > SHEET_MAX_COLUMNS || xmlSheetNumOfColumns < 1)  {
            throw new IllegalArgumentException(
                    "Invalid sheet layout received from file!\n" +
                    "Expected number of rows between 1-" + SHEET_MAX_ROWS + " but received " + xmlSheetNumOfRows + "\n" +
                    "Expected number of columns between 1-" + SHEET_MAX_COLUMNS + " but received " + xmlSheetNumOfColumns);
        }
    }

    private Sheet convertXMLSheetToMySheetObject(String uploadedBy, STLSheet sheetFromFile) {
        Sheet sheet = new SheetImpl();
        sheet.init(uploadedBy, sheetFromFile);
        return sheet;
    }

    @Override
    public void writeSystemDataToFile(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }

        File file = new File(filePath);
        if (file.exists() && !file.canWrite()) {
            throw new IOException("Cannot write to file: " + filePath + ". Check file permissions.");
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(versionManager.getCurrentVersionSheet());
            out.flush();
        }
    }

    @Override
    public void readSystemDataFromFile(String filePath) throws IOException, ClassNotFoundException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + filePath + ".");
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            Sheet sheetFromFile = (Sheet) in.readObject();

            if (sheetFromFile == null) {
                throw new IOException("Failed to read the sheet data from the file: " + filePath + ".");
            }
        }
    }


}
