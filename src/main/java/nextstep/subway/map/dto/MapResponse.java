package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponses;

public class MapResponse {

    private LineResponses lineResponses;

    protected MapResponse() {
    }

    public MapResponse(LineResponses lineResponses) {
        this.lineResponses = lineResponses;
    }

    public LineResponses getLineResponses() {
        return lineResponses;
    }
}
