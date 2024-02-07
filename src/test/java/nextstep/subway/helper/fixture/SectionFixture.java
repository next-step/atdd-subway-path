package nextstep.subway.helper.fixture;

import nextstep.subway.controller.dto.SectionCreateRequestBody;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionFixture {
    public static SectionCreateRequestBody 추가구간_생성_바디(Long upStationId, Long downStationId) {
        return new SectionCreateRequestBody(
                upStationId, downStationId, 10
        );
    }

    public static Section 추가구간_엔티티(Station upStation, Station downStation) {
        return Section.createWithId(2L, upStation, downStation, 10);
    }
}
