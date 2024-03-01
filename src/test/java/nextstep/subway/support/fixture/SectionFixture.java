package nextstep.subway.support.fixture;

import java.util.Map;
import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class SectionFixture {

    public static Map<String, Object> 구간_등록_요청_본문(Long upStationId, Long downStationId, Long distance) {
        return Map.of(
            "upStationId", upStationId,
            "downStationId", downStationId,
            "distance", distance
        );
    }

    public static Section giveOne(Line line, Station upStation, Station downStation, long distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public static Section giveOne(long id, Line line, Station upStation, Station downStation, long distance) {
        Section section = new Section(line, upStation, downStation, distance);
        ReflectionTestUtils.setField(section, "id", id);
        return section;
    }
}


