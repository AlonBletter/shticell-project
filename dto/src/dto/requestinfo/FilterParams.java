package dto.requestinfo;

import java.util.List;
import java.util.Map;

public record FilterParams(
        String rangeToFilter,
        Map<String, List<String>> filterRequestValues
) {
}
