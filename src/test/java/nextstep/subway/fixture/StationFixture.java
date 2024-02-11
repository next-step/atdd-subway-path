package nextstep.subway.fixture;

import nextstep.subway.controller.dto.StationCreateRequest;
import nextstep.subway.domain.Station;

public enum StationFixture {
    GANGNAM_STATION("강남역"),
    SEOLLEUNG_STATION("선릉역"),
    YANGJAE_STATION("양재역"),
    YEOKSAM_STATION("역삼역"),
    SINDAEBANG_STATION("신대방역");

    private final String name;

    StationFixture(String name) {
        this.name = name;
    }

    public StationCreateRequest toCreateRequest() {
        return new StationCreateRequest(name);
    }

    public Station toStation(long id) {
        return new Station(id, name);
    }
}
