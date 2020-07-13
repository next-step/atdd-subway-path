package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.List;

public class MapResponse {
    private List<LineResponse> lines;

    public static MapResponse of(List<LineResponse> lines) {
        return new MapResponse(lines);
    }

    public List<LineResponse> getLines() {
        return lines;
    }

    public MapResponse(List<LineResponse> lines) {
        this.lines = lines;
    }

    public MapResponse() {
    }
}
