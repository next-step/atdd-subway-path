package nextstep.subway.unit;

import nextstep.marker.ClassicUnitTest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.vo.Path;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenCode;

@ClassicUnitTest
public class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathFinder pathFinder;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    void setUp() {
        교대역 = getStation("교대역");
        강남역 = getStation("강남역");
        양재역 = getStation("양재역");
        남부터미널역 = getStation("남부터미널역");

        이호선 = getLine("2호선", "green", 교대역, 강남역, 10L);
        신분당선 = getLine("신분당선", "red", 강남역, 양재역, 10L);
        삼호선 = getLine("3호선", "orange", 교대역, 남부터미널역, 2L);

        createSection(삼호선, 남부터미널역, 양재역, 3L);
    }

    @Nested
    class Success {

        @Test
        void 교대역에서_양재역을_가는_최단_경로는_교대_남부터미널_양재_5미터이다() {
            // when
            Path shortestPath = pathFinder.getShortestPath(교대역, 양재역);

            // then
            verifyShortestPath(shortestPath, 5L, "교대역", "남부터미널역", "양재역");
        }

        @Test
        void 강남역에서_남부터미널역을_가는_최단_경로는_강남_교대_남부터미널_12미터이다() {
            // when
            Path shortestPath = pathFinder.getShortestPath(강남역, 남부터미널역);

            // then
            verifyShortestPath(shortestPath, 12L, "강남역", "교대역", "남부터미널역");
        }

        @Test
        void 강남역에서_양재역을_가는_최단_경로는_강남_양재_10미터이다() {
            // when
            Path shortestPath = pathFinder.getShortestPath(강남역, 양재역);

            // then
            verifyShortestPath(shortestPath, 10L, "강남역", "양재역");
        }

        private void verifyShortestPath(Path shortestPath, long distance, String... stationNames) {
            Assertions.assertEquals(distance, shortestPath.getDistance());
            assertThat(shortestPath.getStations()).hasSize(stationNames.length)
                    .map(Station::getName)
                    .containsExactly(stationNames);
        }

    }

    @Nested
    class Fail {

        @Test
        void 출발역과_도착역이_같은_경우() {
            // when & then
            thenCode(() -> pathFinder.getShortestPath(강남역, 강남역)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 출발역과_도착역이_연결되지_않은_경우() {
            // given
            Station 다른역 = getStation("다른역");

            // when & then
            thenCode(() -> pathFinder.getShortestPath(강남역, 다른역)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    private Line getLine(String name, String color, Station upStation, Station downStation, Long distance) {
        Line line = Line.builder()
                .name(name)
                .color(color)
                .distance(distance)
                .upStation(upStation)
                .downStation(downStation)
                .build();
        return lineRepository.save(line);

    }

    private Station getStation(String name) {
        Station station = Station.create(() -> name);
        return stationRepository.save(station);
    }

    private void createSection(Line line, Station upStation, Station downStation, Long distance) {
        line.add(Section.of(upStation, downStation, distance));
    }
}
