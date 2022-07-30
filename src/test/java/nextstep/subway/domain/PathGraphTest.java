package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotConnectedPathException;
import nextstep.subway.domain.exception.NotEnoughStationsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static nextstep.subway.domain.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        assertThatThrownBy(() -> pathGraph.findShortestPath(GANGNAM_STATION.getId(), GANGNAM_STATION.getId()))
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
        assertThatThrownBy(() -> pathGraph.findShortestPath(GANGNAM_STATION.getId(), NAMBU_BUS_TERMINAL_STATION.getId()))
                .isInstanceOf(NotConnectedPathException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다. 출발역=강남역, 도착역=남부터미널역");
    }

    @Test
    @DisplayName("출발역과 도착역이 모두 노선에 등록되어 있어야한다.")
    void not_found_source() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(GANGNAM_STATION, YEOKSAM_STATION, 10);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(YANGJAE_STATION, NAMBU_BUS_TERMINAL_STATION, 10);

        PathGraph pathGraph = PathGraph.valueOf(createLines(greenLine, orangeLine));

        // when
        assertAll(() -> {
            assertThatThrownBy(() -> pathGraph.findShortestPath(GANGNAM_STATION.getId(), YANGJAE_STATION.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> pathGraph.findShortestPath(YANGJAE_STATION.getId(), GANGNAM_STATION.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> pathGraph.findShortestPath(YANGJAE_STATION.getId(), SEOLLEUNG_STATION.getId()))
                    .isInstanceOf(IllegalArgumentException.class);
        });
    }
    
    /**
     * 양재역  ---  *3호선*  ---  남부터미널역  ---  *3호선*  ---  교대역
     */
    @Test
    @DisplayName("단일 노선 구조에서 최소경로를 찾으면 최소경로를 찾는다.")
    void findShortPath() {
        // given
        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(YANGJAE_STATION, NAMBU_BUS_TERMINAL_STATION, 5);
        orangeLine.addSection(NAMBU_BUS_TERMINAL_STATION, SEOUL_UNIV_EDUCATION_STATION, 10);

        PathGraph pathGraph = PathGraph.valueOf(createLines(orangeLine));

        // when
        Path shortPath = pathGraph.findShortestPath(YANGJAE_STATION.getId(), NAMBU_BUS_TERMINAL_STATION.getId());

        //then
        assertAll(() -> {
            assertThat(shortPath.getStations()).containsExactly(YANGJAE_STATION, NAMBU_BUS_TERMINAL_STATION);
            assertThat(shortPath.getDistance()).isEqualTo(Distance.valueOf(5));
        });
    }

    /**
     * 양재역  ---  *3호선*  ---  남부터미널역  ---  *3호선*  ---  교대역
     *                                                             |
     *                                                             *2호선*
     *                                                             |
     *                                                             강남역  ---  *2호선*  ---  역삼역
     */
    @Test
    @DisplayName("환승구조에서 최소경로를 찾으면 최소경로를 찾는다.")
    void findShortPath_transfer() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(SEOUL_UNIV_EDUCATION_STATION, GANGNAM_STATION, 4);
        greenLine.addSection(GANGNAM_STATION, YEOKSAM_STATION, 6);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(YANGJAE_STATION, NAMBU_BUS_TERMINAL_STATION, 5);
        orangeLine.addSection(NAMBU_BUS_TERMINAL_STATION, SEOUL_UNIV_EDUCATION_STATION, 10);

        PathGraph pathGraph = PathGraph.valueOf(createLines(greenLine, orangeLine));

        // when
        Path shortPath = pathGraph.findShortestPath(YEOKSAM_STATION.getId(), NAMBU_BUS_TERMINAL_STATION.getId());

        //then
        assertAll(() -> {
            assertThat(shortPath.getStations()).containsExactly(YEOKSAM_STATION, GANGNAM_STATION, SEOUL_UNIV_EDUCATION_STATION, NAMBU_BUS_TERMINAL_STATION);
            assertThat(shortPath.getDistance()).isEqualTo(Distance.valueOf(20));
        });
    }

    /**
     * 교대역        ---   *2호선*   ---   강남역
     * |                                   |
     * *3호선*                             *신분당선*
     * |                                   |
     * 남부터미널역  ---   *3호선*   ---   양재역
     */
    @Test
    @DisplayName("순환구조에서 최소경로를 찾으면 최소경로를 찾는다.")
    void findShortPath_cycle() {
        // given
        Line greenLine = new Line("2호선", "bg-green-600");
        greenLine.addSection(SEOUL_UNIV_EDUCATION_STATION, GANGNAM_STATION, 4);

        Line orangeLine = new Line("3호선", "bg-orange-600");
        orangeLine.addSection(SEOUL_UNIV_EDUCATION_STATION, NAMBU_BUS_TERMINAL_STATION, 5);
        orangeLine.addSection(NAMBU_BUS_TERMINAL_STATION, YANGJAE_STATION, 7);

        Line redLine = new Line("신분당선", "bg-red-600");
        redLine.addSection(GANGNAM_STATION, YANGJAE_STATION, 20);

        PathGraph pathGraph = PathGraph.valueOf(createLines(greenLine, orangeLine));

        // when
        Path shortPath = pathGraph.findShortestPath(GANGNAM_STATION.getId(), YANGJAE_STATION.getId());

        //then
        assertAll(() -> {
            assertThat(shortPath.getStations()).containsExactly(GANGNAM_STATION, SEOUL_UNIV_EDUCATION_STATION, NAMBU_BUS_TERMINAL_STATION, YANGJAE_STATION);
            assertThat(shortPath.getDistance()).isEqualTo(Distance.valueOf(16));
        });
    }

    private Lines createLines(Line... lines) {
        return new Lines(List.of(lines));
    }
}