package nextstep.subway.fixture;

import nextstep.subway.controller.dto.StationCreateRequest;

public enum StationFixture {
    GANGNAM_STATION("강남역"),
    SEOLLEUNG_STATION("선릉역"),
    YANGJAE_STATION("양재역");

    private final String name;

    StationFixture(String name) {
        this.name = name;
    }

    public StationCreateRequest toCreateRequest() {
        return new StationCreateRequest(name);
    }
}
