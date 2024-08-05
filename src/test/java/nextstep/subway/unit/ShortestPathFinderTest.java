package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.line.domain.entity.LineSections;
import nextstep.subway.path.application.ShortestPathFinder;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShortestPathFinderTest {

    private ShortestPathFinder shortestPathFinder;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private List<Line> lines;

    @BeforeEach
    void setUp() {
        shortestPathFinder = new ShortestPathFinder();

        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        Line 이호선 = new Line("이호선", "bg-red-600", new LineSections());
        Line 신분당선 = new Line("신분당선", "bg-green-600", new LineSections());
        Line 삼호선 = new Line("삼호선", "bg-orange-600", new LineSections());

        이호선.addSection(new LineSection(이호선, 교대역, 강남역, 10L));
        신분당선.addSection(new LineSection(신분당선, 강남역, 양재역, 10L));
        삼호선.addSection(new LineSection(삼호선, 교대역, 남부터미널역, 2L));
        삼호선.addSection(new LineSection(삼호선, 남부터미널역, 양재역, 10L));

        lines = Arrays.asList(이호선, 신분당선, 삼호선);
    }

    @Test
    @DisplayName("유효한 출발역과 도착역이 주어지면 최단 경로를 반환한다")
    void it_returns_shortest_path() {
        // given
        Station source = 교대역;
        Station target = 양재역;

        // when
        PathResponse pathResponse = shortestPathFinder.find(lines, source, target);

        // then
        assertThat(pathResponse.getStations()).extracting("name")
            .containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName());
        assertThat(pathResponse.getDistance()).isEqualTo(12L);
    }

    @Test
    @DisplayName("출발역과 도착역이 동일하면 예외를 던진다")
    void it_throws_exception_when_source_equals_target() {
        // given
        Station source = 교대역;
        Station target = 교대역;

        // when, then
        assertThatThrownBy(() -> shortestPathFinder.find(lines, source, target))
            .isInstanceOf(SubwayException.class)
            .hasMessageContaining(SubwayExceptionType.SOURCE_AND_TARGET_SAME.getMessage());
    }

    @Test
    @DisplayName("그래프에 출발역이 존재하지 않으면 예외를 던진다")
    void it_throws_exception_when_source_station_not_found() {
        // given
        Station source = new Station("마포역");
        Station target = 양재역;

        // when, then
        assertThatThrownBy(() -> shortestPathFinder.find(lines, source, target))
            .isInstanceOf(SubwayException.class)
            .hasMessageContaining(SubwayExceptionType.PATH_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("그래프에 도착역이 존재하지 않으면 예외를 던진다")
    void it_throws_exception_when_target_station_not_found() {
        // given
        Station source = 교대역;
        Station target = new Station("마포역");

        // when, then
        assertThatThrownBy(() -> shortestPathFinder.find(lines, source, target))
            .isInstanceOf(SubwayException.class)
            .hasMessageContaining(SubwayExceptionType.PATH_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("출발역과 도착역 사이에 경로가 없으면 예외를 던진다")
    void it_throws_exception_when_no_path_between_stations() {
        // given
        Station 공덕역 = new Station("공덕역");
        Station 마포역 = new Station("마포역");
        Line 오호선 = new Line("오호선", "bg-purple-600", new LineSections());
        오호선.addSection(new LineSection(오호선, 공덕역, 마포역, 5L));
        List<Line> updatedLines = new ArrayList<>(lines);
        updatedLines.add(오호선);

        Station source = 교대역;
        Station target = 공덕역;

        // when, then
        assertThatThrownBy(() -> shortestPathFinder.find(updatedLines, source, target))
            .isInstanceOf(SubwayException.class)
            .hasMessageContaining(SubwayExceptionType.PATH_NOT_FOUND.getMessage());
    }
}
