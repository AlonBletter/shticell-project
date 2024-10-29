package dto.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dto.cell.CellStyleDTO;

import java.io.IOException;

public class CellStyleDTOAdapter extends TypeAdapter<CellStyleDTO> {

    @Override
    public void write(JsonWriter out, CellStyleDTO cellStyleDTO) throws IOException {
        out.beginObject();
        out.name("backgroundColor").value(cellStyleDTO.backgroundColor());
        out.name("textColor").value(cellStyleDTO.textColor());
        out.endObject();
    }

    @Override
    public CellStyleDTO read(JsonReader in) throws IOException {
        String backgroundColor = null;
        String textColor = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "backgroundColor":
                    backgroundColor = in.nextString();
                    break;
                case "textColor":
                    textColor = in.nextString();
                    break;
                default:
                    in.skipValue(); // Skip unexpected fields
            }
        }
        in.endObject();

        return new CellStyleDTO(backgroundColor, textColor);
    }
}