package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.List;

public class MapResponse {

    private List<LineResponse> lineResponses;

    public List<LineResponse> getLineResponses() {
        return lineResponses;
    }
}
