package nextstep.subway.station.domain;

public interface StationInspector {
    boolean belongsToLine(Long stationId);
}
