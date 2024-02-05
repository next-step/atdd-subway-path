package nextstep.subway.fixture;

import nextstep.subway.controller.dto.LineCreateRequest;

public enum LineFixture {
    신분당선("신분당선", "bg-red-600", 1L, 2L, 10L),
    분당선("분당선", "bg-green-600", 1L, 2L, 10L);

    private final String lineName;
    private final String lineColor;
    private final Long startStationId;
    private final Long endStationId;
    private final Long distance;

    LineFixture(String lineName, String lineColor, Long startStationId, Long endStationId, Long distance) {
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
        this.distance = distance;
    }

    public LineCreateRequest toCreateRequest(Long startStationId, Long endStationId) {
        return new LineCreateRequest(lineName, lineColor, startStationId, endStationId, distance);
    }
}
