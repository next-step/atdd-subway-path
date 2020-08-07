package nextstep.subway.map.dto;

import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.line.dto.LineStationResponses;

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

    public LineStationResponses getLineStationResponse(LineStationResponses lineStations) {
        LineStationResponses lineStationResponses = new LineStationResponses();
        Long preStationId = stationIds.get(0);

        for (int i = 0; i < stationIds.size(); i++) {
            Long stationId = stationIds.get(i);

            if (stationId == preStationId) continue;

            LineStationResponse lineStationResponse = lineStations.getLineStationResponse(preStationId, stationId);

            lineStationResponses.add(lineStationResponse);
            preStationId = stationId;
        }

        return lineStationResponses;
    }

}
