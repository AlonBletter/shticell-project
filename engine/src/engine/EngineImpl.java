package engine;

import dto.CellConverter;
import dto.CellDTO;
import dto.SheetConverter;
import dto.SheetDTO;
import engine.exception.DataReadException;
import engine.exception.InvalidCellBoundsException;
import engine.generated.STLSheet;
import engine.sheet.api.Sheet;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.impl.SheetImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class EngineImpl implements Engine {
//    public static void main(String[] args) {
//        Engine engine = new EngineImpl();
//
//        try {
//            engine.loadSystemSettingsFromFile("C:\\Users\\Alon-PC\\Downloads\\basic.xml");
//        } catch (DataReadException e) {
//            throw new RuntimeException(e);
//        }
//
//        SheetDTO sheet = engine.getSpreadsheet();
//        Map<Coordinate, CellDTO> cells = sheet.activeCells();
//
//        System.out.println(sheet.name());
//        System.out.println(sheet.numOfRows());
//        System.out.println(sheet.numOfColumns());
//        System.out.println(sheet.rowHeightUnits());
//        System.out.println(sheet.columnWidthUnits());
//
//        for(Map.Entry<Coordinate, CellDTO> entry : cells.entrySet()) {
//            System.out.println("\t" + entry.getKey());
//            System.out.println("\t" + entry.getValue().originalValue());
//            System.out.println("\t" + entry.getValue().effectiveValue());
//        }
//    }
    public static final int SHEET_MAX_COLUMNS = 20;
    public static final int SHEET_MAX_ROWS = 50;

    private Sheet sheet;

    @Override
    public SheetDTO getSpreadsheet() {
        validateLoadedSheet();
        return SheetConverter.convertToDTO(sheet);
    }

    private void validateLoadedSheet() {
        if(sheet == null) {
            throw new IllegalStateException("Sheet isn't loaded yet");
        }
    }

    @Override
    public CellDTO getCell(Coordinate cellToGetCoordinate) {
        validateLoadedSheet();
        return CellConverter.convertToDTO(sheet.getCell(cellToGetCoordinate));
    }

    @Override
    public void updateCell(Coordinate cellToUpdateCoordinate, String newCellOriginalValue) {
        validateLoadedSheet();
        sheet.updateCell(cellToUpdateCoordinate, newCellOriginalValue);
    }

    //TODO GET VERSION + SHEET VALIDATION THAT EXISTS

    @Override
    public void loadSystemSettingsFromFile(String filePath) {
        validateXMLFile(filePath);
        STLSheet sheetFromFile = readSheetFromXMLFile(filePath);
        validateXMLSheetLayout(sheetFromFile);
        sheet = convertXMLSheetToMySheetObject(sheetFromFile);
    }

    private void validateXMLFile(String filePath) {
        if(filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        } else if (filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        } else if (!filePath.endsWith(".xml")) {
            throw new IllegalArgumentException("File path must end with .xml");
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

    private void validateXMLSheetLayout(STLSheet sheetFromFile) {
        int xmlSheetNumOfRows = sheetFromFile.getSTLLayout().getRows();
        int xmlSheetNumOfColumns = sheetFromFile.getSTLLayout().getColumns();

        if(xmlSheetNumOfRows > SHEET_MAX_ROWS || xmlSheetNumOfRows < 1 ||
                xmlSheetNumOfColumns > SHEET_MAX_COLUMNS || xmlSheetNumOfColumns < 1)  {
            throw new IllegalArgumentException(
                    "Invalid sheet layout received from file!\n" +
                    "Expected number of rows between 1-" + SHEET_MAX_ROWS + " but received " + xmlSheetNumOfRows +
                    "\nExpected number of columns between 1-" + SHEET_MAX_COLUMNS + " but received " + xmlSheetNumOfColumns);
        }
    }

    private Sheet convertXMLSheetToMySheetObject(STLSheet sheetFromFile) {
        Sheet sheet = new SheetImpl();

        try {
            sheet.init(sheetFromFile);
        } catch (IllegalArgumentException e) { //TODO maybe surround the load with try catch and not here...
            throw new IllegalArgumentException("Error while loading file: " + e.getMessage());
        } catch (InvalidCellBoundsException e) {
            throw new InvalidCellBoundsException(e.getActualCoordinate(), e.getSheetNumOfRows(), e.getSheetNumOfColumns(), "Error while loading file: ");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Error while loading file: " + e.getMessage());
        }

        return sheet;
    }
}
