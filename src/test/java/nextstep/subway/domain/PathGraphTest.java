package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotEnoughStationsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static nextstep.subway.domain.fixture.StationFixture.GANGNAM_STATION;
import static nextstep.subway.domain.fixture.StationFixture.YEOKSAM_STATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathGraphTest {

    @Test
    @DisplayName("PathGraph 를 생성하기 위해서는 노선 목록이 있어야 한다.")
    void invalid_empty_lines() {
        // given
        List<Line> lines = Collections.emptyList();

        // when
        assertThatThrownBy(() -> PathGraph.valueOf(lines))
                .isInstanceOf(NotEnoughStationsException.class);
    }

    @Test
    @DisplayName("PathGraph 를 생성하기 위해서는 노선에 지하철역에 최소 2개 이상 포함되어 있어야 한다.")
    void invalid_less_than_station() {
        // given
        Line emptyStationLine = new Line("2호선", "bg-green-600");

        List<Line> lines = new ArrayList<>();
        lines.add(emptyStationLine);

        // when
        assertThatThrownBy(() -> PathGraph.valueOf(lines))
                .isInstanceOf(NotEnoughStationsException.class);
    }

    @Test
    @DisplayName("경로를 조회할 때 출발역과 도착역이 달라야 한다.")
    void name() {
        // given
        Line line = new Line("2호선", "bg-green-600");
        line.addSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        PathGraph pathGraph = PathGraph.valueOf(lines);

        // when
        assertThatThrownBy(() -> pathGraph.findShortPath(GANGNAM_STATION, GANGNAM_STATION))
                .isInstanceOf(IllegalArgumentException.class);
    }
}