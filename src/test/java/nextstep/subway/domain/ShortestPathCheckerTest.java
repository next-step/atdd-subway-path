package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 최단 경로 탐색")
class ShortestPathCheckerTest {

    private Station 교대역;
    private Station 남부터미널역;
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;
    private Station 정자역;
    private Station 미금역;
    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = Station.of("교대역");
        남부터미널역 = Station.of("남부터미널역");
        강남역 = Station.of("강남역");
        양재역 = Station.of("양재역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        미금역 = Station.of("미금역");

        신분당선 = Line.of("신분당선", "red", 강남역, 양재역, 100);
        신분당선.addSection(양재역, 판교역, 100);
        신분당선.addSection(판교역, 정자역, 100);
        신분당선.addSection(정자역, 미금역, 100);

        이호선 = Line.of("이호선", "green", 교대역, 강남역, 50);

        삼호선 = Line.of("삼호선", "orange", 교대역, 남부터미널역, 20);
        삼호선.addSection(남부터미널역, 양재역, 20);
    }

    @DisplayName("노선이 1개 일 때의 최단 경로")
    @Test
    void findShortestPath_1Line() {
        // given
        ShortestPathChecker checker = ShortestPathChecker.of(Arrays.asList(신분당선));

        // when
        List<Station> path = checker.findShortestPath(미금역, 판교역);

        // then
        assertThat(path).containsExactly(미금역, 정자역, 판교역);
    }

    @DisplayName("노선이 2개 일 때의 최단 경로")
    @Test
    void findShortestPath_2Line() {
        // given
        ShortestPathChecker checker = ShortestPathChecker.of(Arrays.asList(신분당선, 이호선));

        // when
        List<Station> path = checker.findShortestPath(판교역, 교대역);

        // then
        assertThat(path).containsExactly(판교역, 양재역, 강남역, 교대역);
    }

    @DisplayName("노선이 3개 일 때의 최단 경로")
    @Test
    void findShortestPath_3Line() {
        // given
        ShortestPathChecker checker = ShortestPathChecker.of(Arrays.asList(신분당선, 이호선, 삼호선));

        // when
        List<Station> path = checker.findShortestPath(판교역, 교대역);

        // then
        assertThat(path).containsExactly(판교역, 양재역, 남부터미널역, 교대역);
    }

}