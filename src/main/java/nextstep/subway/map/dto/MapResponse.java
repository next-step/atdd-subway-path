package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.HashMap;
import java.util.Map;

public class MapResponse {
    Map<String, LineResponse> lineMap = new HashMap<>();

    public Map<String, LineResponse> getLineMap() {
        return lineMap;
    }
}
