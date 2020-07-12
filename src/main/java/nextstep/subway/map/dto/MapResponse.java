package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.List;

public class MapResponse {
    private List<LineResponse> lines;

    public MapResponse() {
    }

    public MapResponse(List<LineResponse> lines) {
        this.lines = lines;
    }

    public List<LineResponse> getLines() {
        return lines;
    }

    public void setLines(List<LineResponse> lines) {
        this.lines = lines;
    }
}
