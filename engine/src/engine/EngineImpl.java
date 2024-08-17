package engine;

import dto.CellDTO;
import dto.SheetDTO;
import engine.exception.DataReadException;
import engine.exception.InvalidCellBoundsException;
import engine.exception.InvalidSheetLayoutException;
import engine.generated.STLCell;
import engine.generated.STLCells;
import engine.generated.STLSheet;
import engine.sheet.api.Sheet;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.List;

public class EngineImpl implements Engine {
    public static final int SHEET_MAX_COLUMNS = 50;
    public static final int SHEET_MAX_ROWS = 20;

    private Sheet sheet;

    @Override
    public SheetDTO getSpreadsheet() {
        return null;
    }

    @Override
    public CellDTO getCell(int row, int column) {
        return null;
    }

    @Override
    public void updateCell(CellDTO updatedCell) {

    }

    @Override
    public void loadSystemSettingsFromFile(String filePath) throws DataReadException {
        validateXMLFile(filePath);
        STLSheet sheetFromFile = readSheetFromXMLFile(filePath);
        validateXMLSheetLayoutAndCells(sheetFromFile);
        convertXMLSheetToCurrentSheet(sheetFromFile);
    }

    private void validateXMLFile(String filePath) {
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        } else if (!filePath.endsWith(".xml")) {
            throw new IllegalArgumentException("File path must end with .xml");
        }
    }

    private STLSheet readSheetFromXMLFile(String filePath) throws DataReadException { // Should be checked or unchecked?
        try {
            File file = new File(filePath);
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (STLSheet) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new DataReadException("Failed to read the sheet from XML file", e);
        }
    }

    private void validateXMLSheetLayoutAndCells(STLSheet sheetFromFile) {
        STLCells cells = sheetFromFile.getSTLCells();
        List<STLCell> cellsList = cells.getSTLCell();
        int xmlSheetNumOfRows = sheetFromFile.getSTLLayout().getRows();
        int xmlSheetNumOfColumns = sheetFromFile.getSTLLayout().getColumns();

        if(xmlSheetNumOfRows > SHEET_MAX_ROWS || xmlSheetNumOfRows < 0 ||
                xmlSheetNumOfColumns > SHEET_MAX_COLUMNS || xmlSheetNumOfColumns < 0)  {
            throw new InvalidSheetLayoutException(xmlSheetNumOfRows, xmlSheetNumOfColumns, SHEET_MAX_ROWS, SHEET_MAX_COLUMNS);
        }

        for (STLCell stlCell : cellsList) {
            Coordinate cellCoordinate = CoordinateFactory.createCoordinate(stlCell.getRow(), stlCell.getColumn());

            if(cellCoordinate.getRow() > xmlSheetNumOfRows || cellCoordinate.getRow() < 0 ||
                    cellCoordinate.getColumn() < 0 || cellCoordinate.getColumn() > xmlSheetNumOfColumns) {
                throw new InvalidCellBoundsException(cellCoordinate, xmlSheetNumOfRows, xmlSheetNumOfColumns);
            }
        }
    }

    private void convertXMLSheetToCurrentSheet(STLSheet sheetFromFile) {
    }


}
