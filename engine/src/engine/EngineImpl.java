package engine;

import dto.CellDTO;
import dto.SheetDTO;
import engine.exception.DataReadException;
import engine.exception.InvalidSheetLayoutException;
import engine.generated.STLCell;
import engine.generated.STLCells;
import engine.generated.STLSheet;
import engine.sheet.api.Sheet;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.impl.SheetImpl;
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
        //TODO
        return null;
    }

    @Override
    public CellDTO getCell(int row, int column) {
        //TODO
        return null;
    }

    @Override
    public void updateCell(String cellToUpdate, String newCellOriginalValue) {
        Coordinate cellToUpdateCoordinate = CoordinateFactory.createCoordinate(cellToUpdate);
        sheet.updateCell(cellToUpdateCoordinate, newCellOriginalValue);
    }

    @Override
    public void loadSystemSettingsFromFile(String filePath) throws DataReadException {
        validateXMLFile(filePath);
        STLSheet sheetFromFile = readSheetFromXMLFile(filePath);
        validateXMLSheetLayoutAndCells(sheetFromFile);
        convertXMLSheetToCurrentSheet(sheetFromFile);
    }

    private void validateXMLFile(String filePath) {
        if(filePath == null || filePath.isEmpty()) { //TODO: Think of other possibilities for exception
            throw new IllegalArgumentException("File path cannot be null or empty");
        } else if (!filePath.endsWith(".xml")) {
            throw new IllegalArgumentException("File path must end with .xml");
        }
    }

    private STLSheet readSheetFromXMLFile(String filePath) throws DataReadException { //TODO: Should be checked or unchecked?
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
            SheetImpl.validateCoordinateInbound(cellCoordinate, xmlSheetNumOfRows, xmlSheetNumOfColumns);
        }

        validateXMLCellReferences(sheetFromFile);
    }

    private void validateXMLCellReferences(STLSheet sheetFromFile) {
        //TODO
    }

    private void convertXMLSheetToCurrentSheet(STLSheet sheetFromFile) {
        sheet.setName(sheetFromFile.getName());
        sheet.setNumberOfRows(sheetFromFile.getSTLLayout().getRows());
        sheet.setNumberOfColumns(sheetFromFile.getSTLLayout().getColumns());
        sheet.setRowHeightUnits(sheetFromFile.getSTLLayout().getSTLSize().getRowsHeightUnits());
        sheet.setColumnWidthUnits(sheetFromFile.getSTLLayout().getSTLSize().getColumnWidthUnits());
        sheet.resetActiveCells();

        STLCells cellsFromFile = sheetFromFile.getSTLCells();
        List<STLCell> cellsList = cellsFromFile.getSTLCell();

        for (STLCell stlCell : cellsList) {
            Coordinate cellCoordinate = CoordinateFactory.createCoordinate(stlCell.getRow(), stlCell.getColumn());
            sheet.updateCell(cellCoordinate, stlCell.getSTLOriginalValue());
        }

        //TODO MAHAM + dependencies
    }


}
