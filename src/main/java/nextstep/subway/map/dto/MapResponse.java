package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.List;

public class MapResponse {
    private final List<LineResponse> lines;

    private MapResponse(List<LineResponse> lines) {

        this.lines = lines;
    }

    public List<LineResponse> getLines() {
        return lines;
    }

    public static MapResponse with(List<LineResponse> allLines) {
        return new MapResponse(allLines);
    }
}
