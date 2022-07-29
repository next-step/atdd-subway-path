package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotConnectedPathException;
import nextstep.subway.domain.exception.NotEnoughStationsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static nextstep.subway.domain.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathGraphTest {

    @Test
    @DisplayName("PathGraph 를 생성하기 위해서는 노선 목록이 있어야 한다.")
    void invalid_empty_lines() {
        // given
        List<Line> lines = Collections.emptyList();

        // when
        assertThatThrownBy(() -> PathGraph.valueOf(new Lines(lines)))
                .isInstanceOf(NotEnoughStationsException.class);
    }

    @Test
    @DisplayName("PathGraph 를 생성하기 위해서는 노선에 지하철역에 최소 2개 이상 포함되어 있어야 한다.")
    void invalid_less_than_station() {
        // given
        Line emptyStationLine = new Line("2호선", "bg-green-600");

        // when
        assertThatThrownBy(() -> PathGraph.valueOf(createLines(emptyStationLine)))
                .isInstanceOf(NotEnoughStationsException.class);
    }

    @Test
    @DisplayName("경로를 조회할 때 출발역과 도착역이 달라야 한다.")
    void not_same_source_and_target() {
        // given
        Line line = new Line("2호선", "bg-green-600");
        line.addSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        PathGraph pathGraph = PathGraph.valueOf(createLines(line));

        // when
        assertThatThrownBy(() -> pathGraph.findShortPath(GANGNAM_STATION, GANGNAM_STATION))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("출발역과 도착역이 연결이 되어있어야 한다.")
    void not_connected_source_and_target() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(YANGJAE_STATION, NAMBU_BUS_TERMINAL_STATION, 10);

        PathGraph pathGraph = PathGraph.valueOf(createLines(greenLine, orangeLine));

        // when
        assertThatThrownBy(() -> pathGraph.findShortPath(GANGNAM_STATION, NAMBU_BUS_TERMINAL_STATION))
                .isInstanceOf(NotConnectedPathException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다. 출발역=강남역, 도착역=남부터미널역");
    }

    private Lines createLines(Line... lines) {
        return new Lines(List.of(lines));
    }
}