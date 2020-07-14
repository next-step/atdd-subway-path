package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.ArrayList;
import java.util.List;

public class MapResponse {

    private final List<LineResponse> lineResponses;

    public MapResponse() {
        lineResponses = new ArrayList<>();
    }

    public MapResponse(List<LineResponse> lineResponses) {
        this.lineResponses = lineResponses;
    }

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }
}
