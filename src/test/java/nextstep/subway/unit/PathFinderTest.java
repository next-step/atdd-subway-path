package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathStationResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.vo.DijkstraPathFinder;
import nextstep.subway.vo.PathFinder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {
    Station 신논역;
    Station 신사역;
    Station 강남역;
    Station 판교역;
    Station 논현역;
    Station 삼성역;
    Station 개탄역;
    Station 망해역;
    Station 둥섬역;
    List<Station> stations;
    List<Section> sections;

    @BeforeEach
    void setup() {
        신논역 = new Station(1L, "신논역");
        신사역 = new Station(2L, "신사역");
        강남역 = new Station(3L, "강남역");
        판교역 = new Station(4L, "판교역");
        논현역 = new Station(5L, "논현역");
        삼성역 = new Station(6L, "삼성역");
        개탄역 = new Station(7L, "개탄역");
        망해역 = new Station(8L, "망해역");
        둥섬역 = new Station(9L, "둥섬역");

        Line line = new Line(1L, "line", "color");

        Section section1 = new Section(1L, line, 신논역, 신사역, 10);
        Section section2 = new Section(2L, line, 신사역, 강남역, 10);
        Section section3 = new Section(3L, line, 강남역, 판교역, 10);
        Section section4 = new Section(4L, line, 판교역, 논현역, 10);
        Section section5 = new Section(5L, line, 논현역, 삼성역, 10);

        stations = List.of(신논역, 신사역, 강남역, 판교역, 논현역, 삼성역, 개탄역);
        sections = List.of(section1, section2, section3, section4, section5);
    }

    @Test
    @DisplayName("경로 찾기 정상 테스트")
    void test() {
        PathFinder pathFinder = new DijkstraPathFinder(stations, sections);
        PathResponse response = pathFinder.findPath(신논역, 삼성역);
        assertThat(response.getDistance()).isEqualTo(50L);
        assertThat(response.getStations()).extracting(PathStationResponse::getId).containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
    }

    @Test
    @DisplayName("경로찾기 : 출발역과 도착역이 같은 경우")
    void test1() {
        PathFinder pathFinder = new DijkstraPathFinder(stations, sections);
        assertThatThrownBy(() -> {
            pathFinder.findPath(신논역, 신논역);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("경로찾기 : 출발역과 도착역이 연결이 되어 있지 않은 경우")
    void test2() {
        PathFinder pathFinder = new DijkstraPathFinder(stations, sections);
        assertThatThrownBy(() -> {
            pathFinder.findPath(신논역, 개탄역);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    void test3() {
        PathFinder pathFinder = new DijkstraPathFinder(stations, sections);
        assertThatThrownBy(() -> {
            pathFinder.findPath(망해역, 둥섬역);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}
