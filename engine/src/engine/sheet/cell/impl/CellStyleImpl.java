package engine.sheet.cell.impl;

import engine.sheet.cell.api.CellStyle;

import java.io.Serializable;
import java.util.Objects;

public class CellStyleImpl implements CellStyle, Serializable {
    private String backgroundColor;

    private String textColor;
    public CellStyleImpl(String backgroundColor, String textColor) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    public CellStyleImpl() {
        this.backgroundColor = null;
        this.textColor = null;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellStyleImpl cellStyle = (CellStyleImpl) o;
        return Objects.equals(backgroundColor, cellStyle.backgroundColor) && Objects.equals(textColor, cellStyle.textColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroundColor, textColor);
    }
}