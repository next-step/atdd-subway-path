package nextstep.subway.unit;

import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PathFinder 테스트")
class PathFinderTest {

    private Station 강남역, 역삼역, 잠실역, 교대역;
    @BeforeEach
    void setUp() {
        강남역 = createStation(1L, "강남역");
        역삼역 = createStation(2L, "역삼역");
        잠실역 = createStation(3L, "잠실역");
        교대역 = createStation(3L, "교대역");
    }

    @DisplayName("PathFinder의 graph에 정점(vertext)이 각 역으로 정상적으로 추가되는지 확인")
    @Test
    void addPath() {
        // given : 선행조건 기술
        Line line = createLine(강남역, 역삼역, 2);
        Line line2 = createLine(역삼역, 잠실역, 3);
        List<Line> lines = List.of(line, line2);

        // when : 기능 수행
        PathFinder pathFinder = new PathFinder(lines);

        // then : 결과 확인
        assertThat(pathFinder.getGraph().containsVertex(강남역)).isTrue();
        assertThat(pathFinder.getGraph().containsVertex(역삼역)).isTrue();
        assertThat(pathFinder.getGraph().containsVertex(잠실역)).isTrue();
    }

    @DisplayName("PathFinder의 경로를 조회할시 정상적으로 역과 거리가 조회되는지 확인")
    @Test
    void findPath() {
        Line line = createLine(강남역, 역삼역, 2);
        Line line2 = createLine(역삼역, 잠실역, 3);
        List<Line> lines = List.of(line, line2);
        PathFinder pathFinder = new PathFinder(lines);

        // when : 기능 수행
        Sections sections = pathFinder.findPath(강남역, 잠실역);

        // then : 결과 확인
        assertThat(sections.getStations()).containsExactly(강남역, 역삼역, 잠실역);
        assertThat(sections.getDistance()).isEqualTo(5);
    }

    @DisplayName("PathFinder의 경로를 조회할시 출발역과 도착역이 같을시 예외 발생")
    @Test
    void findPath_thenReturnException_EQUALS_STATIONS() {
        // given : 선행조건 기술
        Line line = createLine(강남역, 역삼역, 2);
        Line line2 = createLine(잠실역, 교대역, 3);
        List<Line> lines = List.of(line, line2);
        PathFinder pathFinder = new PathFinder(lines);

        // when : 기능 수행, then : 결과 확인
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 강남역))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.EQUALS_STATIONS.getMessage());
    }

    @DisplayName("PathFinder의 경로를 조회할시 조회가 되지 않을때 예외 발생")
    @Test
    void findPath_thenReturnException_NOT_FOUND_PATH() {
        // given : 선행조건 기술
        Line line = createLine(강남역, 역삼역, 2);
        Line line2 = createLine(잠실역, 교대역, 3);
        List<Line> lines = List.of(line, line2);
        PathFinder pathFinder = new PathFinder(lines);

        // when : 기능 수행, then : 결과 확인
        assertThatThrownBy(() -> pathFinder.findPath(강남역, 잠실역))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.NOT_FOUND_PATH.getMessage());
    }

    private Line createLine(Station upStation, Station downStation, int distance) {
        return Line.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .section(section(upStation, downStation, distance))
                .build();
    }

    private Station createStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);

        return station;
    }

    private Section section(Station upStation, Station downStation, int distance) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}