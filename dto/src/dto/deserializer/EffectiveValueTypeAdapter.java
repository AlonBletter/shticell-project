package dto.deserializer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import engine.sheet.effectivevalue.EffectiveValueImpl;
import engine.sheet.effectivevalue.EffectiveValue;
import engine.sheet.cell.api.CellType;

import java.io.IOException;

public class EffectiveValueTypeAdapter extends TypeAdapter<EffectiveValue> {
    @Override
    public void write(JsonWriter out, EffectiveValue effectiveValue) throws IOException {
        out.beginObject();
        out.name("cellType").value(effectiveValue.cellType().toString());
        out.name("value");

        // Use Gson to serialize the value based on its actual type
        Gson gson = new Gson();
        gson.toJson(effectiveValue.value(), effectiveValue.value().getClass(), out);

        out.endObject();
    }

    @Override
    public EffectiveValueImpl read(JsonReader in) throws IOException {
        CellType cellType = null;
        Object value = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "cellType":
                    cellType = CellType.valueOf(in.nextString().toUpperCase());
                    break;
                case "value":
                    // Deserialize the value based on the cell type or other criteria
                    value = deserializeValue(in, cellType); // Custom method for deserialization based on type
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();

        return new EffectiveValueImpl(cellType, value);
    }

    private Object deserializeValue(JsonReader in, CellType cellType) throws IOException {
        return switch (cellType) {
            case NUMERIC -> in.nextDouble();
            case BOOLEAN -> in.nextBoolean();
            default -> in.nextString();
        };
    }
}
