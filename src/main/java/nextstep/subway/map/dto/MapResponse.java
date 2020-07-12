package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.ArrayList;
import java.util.List;

public class MapResponse {
    private final List<LineResponse> lineResponses;

    protected MapResponse() {
        this.lineResponses = new ArrayList<>();
    }

    private MapResponse(List<LineResponse> lineResponses) {

        this.lineResponses = lineResponses;
    }

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }

    public static MapResponse with(List<LineResponse> allLines) {
        return new MapResponse(allLines);
    }
}
