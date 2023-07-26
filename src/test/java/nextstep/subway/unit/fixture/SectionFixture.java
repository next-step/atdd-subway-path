package nextstep.subway.unit.fixture;

import nextstep.subway.section.repository.Section;

public class SectionFixture {
    public static Section 강남역_TO_신논현역() {
        return Section.builder()
                .upStation(StationFixture.강남역())
                .downStation(StationFixture.신논현역())
                .distance(10L)
                .build();
    }

    public static Section 신논현역_TO_논현역() {
        return Section.builder()
                .upStation(StationFixture.신논현역())
                .downStation(StationFixture.논현역())
                .distance(5L)
                .build();
    }
}
