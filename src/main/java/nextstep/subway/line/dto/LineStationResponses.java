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

    public void add(LineStationResponse lineStationResponse) {
        this.lineStationResponses.add(lineStationResponse);
    }

    public void removeLineStationResponse(LineStationResponse lineStationResponse) {
        this.lineStationResponses.remove(lineStationResponse);
    }

    public int getResponseSize() {
        return this.lineStationResponses.size();
    }

    public LineStationResponse getLineStationResponse(Long preStationId, Long stationId) {

        for (LineStationResponse lineStationResponse : this.lineStationResponses) {
            if (isSameStation(stationId, preStationId, lineStationResponse)) {
                return lineStationResponse;
            }
        }
        throw new RuntimeException();
    }

    private boolean isSameStation(Long stationId, Long preStationId, LineStationResponse it) {
        return (it.getPreStationId() == preStationId && it.getStation().getId() == stationId)
                || (it.getPreStationId() == stationId && it.getStation().getId() == preStationId);
    }

}
