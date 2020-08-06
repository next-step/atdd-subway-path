package nextstep.subway.line.dto;

import java.util.ArrayList;
import java.util.List;

public class LineStationResponses {

    private List<LineStationResponse> lineStationResponses;

    public LineStationResponses() {
        this.lineStationResponses = new ArrayList<>();
    }

    public LineStationResponses(List<LineStationResponse> lineStationResponses) {
        this.lineStationResponses = lineStationResponses;
    }

    public List<LineStationResponse> getLineStationResponses() {
        return new ArrayList<>(lineStationResponses);
    }
}
