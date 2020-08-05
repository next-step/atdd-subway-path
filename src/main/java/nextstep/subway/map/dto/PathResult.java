package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineStationResponse;

import java.util.ArrayList;
import java.util.List;

public class PathResult {
    private List<Long> stationIds;
    private double weight;

    public PathResult(List<Long> stationIds, double weight) {
        this.stationIds = stationIds;
        this.weight = weight;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public double getWeight() {
        return weight;
    }

    public List<LineStationResponse> getLineStationResponse(List<LineStationResponse> lineStations) {
        List<LineStationResponse> lineStationResponses = new ArrayList<>();
        Long preStationId = stationIds.get(0);

        for (int i = 0; i < stationIds.size(); i++) {
            Long stationId = stationIds.get(i);

            if (stationId == preStationId) continue;

            LineStationResponse lineStationResponse = getLineStations(lineStations, preStationId, stationId);

            lineStationResponses.add(lineStationResponse);
            preStationId = stationId;
        }

        return lineStationResponses;
    }

    private LineStationResponse getLineStations(List<LineStationResponse> lineStations, Long preStationId, Long stationId) {

        for (LineStationResponse lineStationResponse : lineStations) {
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
