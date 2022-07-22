package nextstep.subway.ui.dto;

import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {

    private final List<Station> stationList;
    private final long distance;

    public PathResponse(final List<Station> stationList, final long distance) {
        this.stationList = stationList;
        this.distance = distance;
    }
}
