package gui.singlecell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CellModel {
    protected SimpleStringProperty value;

    public CellModel(String value) {
        this.value = new SimpleStringProperty(value);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public StringProperty valueProperty() {
        return value;
    }
}
