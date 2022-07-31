package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private static final Long ONE_ID = 1L;
    private static final Long TWO_ID = 2L;
    private static final Long THREE_ID = 3L;
    private static final String LINE_ONE_NM = "일호선";
    private static final String LINE_ONE_COLOR = "blue";
    private static final String UP_STATION_NM_= "신도림";
    private static final String DOWN_STATION_NM = "가산디지털단지";
    private static final String ADDITIONAL_STATION_NM = "독산";
    private static final int DISTANCE_DEFAULT = 10;

    private final Line 일호선 = new Line(ONE_ID, LINE_ONE_NM, LINE_ONE_COLOR);
    private final Station 신도림 = new Station(ONE_ID, UP_STATION_NM_);
    private final Station 가산디지털단지 = new Station(TWO_ID, DOWN_STATION_NM);
    private final Station 독산 = new Station(THREE_ID, ADDITIONAL_STATION_NM);

    @Test
    @DisplayName("구간을 생성한다.")
    void addSection() {
        // when
        일호선.addSection(신도림, 가산디지털단지, DISTANCE_DEFAULT);

        // then
        assertThat(일호선.getSections()).filteredOn(
                section -> section.equals(new Section(일호선, 신도림, 가산디지털단지, DISTANCE_DEFAULT))
        );
    }

    @Test
    @DisplayName("노선의 역을 조회한다.")
    void getStations() {
        // given
        일호선.addSection(신도림, 가산디지털단지, DISTANCE_DEFAULT);

        // when
        List<Station> stations = 일호선.getStations();

        // then
        assertThat(stations).containsExactly(신도림, 가산디지털단지);
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void removeSection() {
        // given
        일호선.addSection(신도림, 가산디지털단지, DISTANCE_DEFAULT);
        일호선.addSection(가산디지털단지, 독산, DISTANCE_DEFAULT);

        // when
        Station 일호선_종착역 = 일호선.getLastStation();
        일호선.removeSection(일호선_종착역.getId());

        // then
        assertThat(일호선.getSections()).hasSize(1);
    }
}
