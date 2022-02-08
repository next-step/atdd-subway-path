package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.test.util.ReflectionTestUtils;

public class PathFixture {

    public static Station 교대역 = createStation(1L, "교대역");
    public static Station 강남역 = createStation(2L, "강남역");
    public static Station 남터역 = createStation(3L, "남터역");
    public static Station 양재역 = createStation(4L, "양재역");

    public static Line 이호선 = Line.of("이호선", "green");
    public static Line 삼호선 = Line.of("삼호선", "orange");
    public static Line 신분당선 = Line.of("신분당선", "red");

    /**
     *
     * 교대----강남
     *  |      |
     * 남터    양재
     */
    static {
        이호선.addSection(Section.of(이호선, 교대역, 강남역, 10));
        삼호선.addSection(Section.of(삼호선, 교대역, 남터역, 10));
        신분당선.addSection(Section.of(삼호선, 강남역, 양재역, 10));
    }

    public static Station createStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
