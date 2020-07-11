package nextstep.subway.line.dto;

import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

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

    public static LineStationResponse of(LineStation lineStation) {
        StationResponse stationResponse = StationResponse.of(lineStation.getStation());
        return LineStationResponse.of(lineStation, stationResponse);
    }

    public static LineStationResponse of(LineStation lineStation, StationResponse stationResponse) {
        Long preStationId = null;
        Station preLineStation = lineStation.getPreStation();
        if (preLineStation != null) {
            preStationId = preLineStation.getId();
        }
        return new LineStationResponse(stationResponse, preStationId, lineStation.getDistance(), lineStation.getDuration());
    }

    public static List<LineStationResponse> from(List<LineStation> lineStations) {
        return lineStations.stream()
                .map(lineStation -> {
                    Station station = lineStation.getStation();
                    return LineStationResponse.of(lineStation, StationResponse.of(station));
                }).collect(Collectors.toList());
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
}