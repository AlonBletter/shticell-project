package gui.singlecell;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CellModel {
    protected SimpleStringProperty effectiveValue;

    public CellModel(String effectiveValue) {
        this.effectiveValue = new SimpleStringProperty(effectiveValue);
    }

    public String getEffectiveValue() {
        return effectiveValue.get();
    }

    public void setEffectiveValue(String effectiveValue) {
        this.effectiveValue.set(effectiveValue);
    }

    public StringProperty effectiveValueProperty() {
        return effectiveValue;
    }
}
