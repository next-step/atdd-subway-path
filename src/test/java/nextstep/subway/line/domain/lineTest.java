package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class lineTest {

    static final Long LINE_UP_STATION_ID = 1L;
    static final Long LINE_DOWN_STATION_ID = 2L;
    static final Long NEW_UP_STATION_ID = 3L;
    static final Long NEW_DOWN_STATION_ID = 4L;

    static final Long LINE_FIRST_SECTION_ID = 1L;
    static final Long LINE_SECOND_SECTION_ID = 2L;

    Line 이호선;
    Station 이호선_상행종점역;
    Station 이호선_하행종점역;

    @BeforeEach
    void setUp() {
        Line line = Line.builder()
                .id(1L)
                .name("2호선")
                .color("bg-green")
                .build();

        Station 신도림역 = new Station(LINE_UP_STATION_ID, "신도림역");
        Station 영등포구청역 = new Station(LINE_DOWN_STATION_ID, "영등포구청역");

        Section section = Section.builder()
                .upStation(신도림역)
                .downStation(영등포구청역)
                .line(line)
                .distance(6)
                .build();
        ReflectionTestUtils.setField(section, "id", LINE_FIRST_SECTION_ID);
        line.addSection(section);

        이호선 = line;
        이호선_상행종점역 = 신도림역;
        이호선_하행종점역 = 영등포구청역;
    }

    @Test
    void 새로운구간_상행역이_노선의_상행종점역이_된다() {
        Station 당산역 = new Station(NEW_UP_STATION_ID, "당산역");

        Section section = Section.builder()
                .line(이호선)
                .upStation(당산역)
                .downStation(이호선_상행종점역)
                .distance(5)
                .build();
        ReflectionTestUtils.setField(section, "id", LINE_SECOND_SECTION_ID);
        이호선.addSection(section);

        assertThat(이호선.getAllStations()).containsExactly(당산역, 이호선_상행종점역, 이호선_하행종점역);
    }

}
