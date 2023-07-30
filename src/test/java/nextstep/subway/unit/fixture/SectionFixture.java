package nextstep.subway.unit.fixture;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class SectionFixture {

    static Long id = 1L;

    public static Section 지하철_구간_생성(Line line, Station upStation, Station downStation, int distance) {
        Section section = new Section(line,upStation,downStation,distance);
        ReflectionTestUtils.setField(section,"id",useId());
        return section;
    }

    public static Long useId(){
        return id++;
    }
}
