package engine;

import dto.CellConverter;
import dto.CellDTO;
import dto.SheetConverter;
import dto.SheetDTO;
import engine.exception.DataReadException;
import engine.exception.InvalidSheetLayoutException;
import engine.generated.STLCell;
import engine.generated.STLCells;
import engine.generated.STLSheet;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.impl.SheetImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;

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
    public static final int SHEET_MAX_COLUMNS = 50;
    public static final int SHEET_MAX_ROWS = 20;

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
        return CellConverter.convertToDTO(sheet.getCell(cellToGetCoordinate));
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
        if(filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        } else if (filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
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

        if(xmlSheetNumOfRows > SHEET_MAX_ROWS || xmlSheetNumOfRows < 1 ||
                xmlSheetNumOfColumns > SHEET_MAX_COLUMNS || xmlSheetNumOfColumns < 1)  {
            throw new IllegalArgumentException(
                    "Sheet layout from the XML file is invalid!" +
                    " Expected row range is 1-" + SHEET_MAX_ROWS + " but received " + xmlSheetNumOfRows +
                    ". Expected column range is 1-" + SHEET_MAX_COLUMNS + " but received " + xmlSheetNumOfColumns);
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
        sheet = new SheetImpl(
                sheetFromFile.getName(),
                sheetFromFile.getSTLLayout().getRows(),
                sheetFromFile.getSTLLayout().getColumns(),
                sheetFromFile.getSTLLayout().getSTLSize().getRowsHeightUnits(),
                sheetFromFile.getSTLLayout().getSTLSize().getColumnWidthUnits(),
                new HashMap<Coordinate, Cell>(),
                new HashMap<Coordinate, Coordinate>()
        );
//        sheet.setName(sheetFromFile.getName());
//        sheet.setNumberOfRows(sheetFromFile.getSTLLayout().getRows());
//        sheet.setNumberOfColumns(sheetFromFile.getSTLLayout().getColumns());
//        sheet.setRowHeightUnits(sheetFromFile.getSTLLayout().getSTLSize().getRowsHeightUnits());
//        sheet.setColumnWidthUnits(sheetFromFile.getSTLLayout().getSTLSize().getColumnWidthUnits());
//        sheet.resetActiveCells();

        STLCells cellsFromFile = sheetFromFile.getSTLCells();
        List<STLCell> cellsList = cellsFromFile.getSTLCell();

        for (STLCell stlCell : cellsList) {
            Coordinate cellCoordinate = CoordinateFactory.createCoordinate(stlCell.getRow(), stlCell.getColumn());
            sheet.updateCell(cellCoordinate, stlCell.getSTLOriginalValue());
        }

        //TODO MAHAM + dependencies
    }


}
