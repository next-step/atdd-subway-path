package nextstep.subway.line.dto;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.dto.StationResponse;

public class LineStationResponse {
    private StationResponse station;
    private Long preStationId;
    private Integer distance;
    private Integer duration;

    public LineStationResponse() {
    }

    public LineStationResponse(StationResponse station, Long preStationId, Integer distance, Integer duration) {
        this.station = station;
        this.preStationId = preStationId;
        this.distance = distance;
        this.duration = duration;
    }

    public static LineStationResponse of(LineStation it, StationResponse station) {
        return new LineStationResponse(station, it.getPreStationId(), it.getDistance(), it.getDuration());
    }

    public StationResponse getStation() {
        return station;
    }

    public Long getPreStationId() {
        return preStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }

    public boolean isMatchedStationResponse(StationResponse stationResponse) {
        return station.equals(stationResponse);
    }
}
