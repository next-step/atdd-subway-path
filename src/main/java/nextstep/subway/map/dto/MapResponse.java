package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.List;

public class MapResponse {
    private List<LineResponse> lineResponses;

    public static MapResponse of(List<LineResponse> lines) {
        return new MapResponse(lines);
    }

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }

    public MapResponse(List<LineResponse> lineResponses) {
        this.lineResponses = lineResponses;
    }

    public MapResponse() {
    }
}
