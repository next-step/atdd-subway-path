package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineResponse;

import java.util.ArrayList;
import java.util.List;

public class MapResponse {
    private final List<LineResponse> lines;

    protected MapResponse() {
        this.lines = new ArrayList<>();
    }

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
