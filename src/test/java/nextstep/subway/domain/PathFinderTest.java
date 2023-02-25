package nextstep.subway.domain;

import nextstep.subway.ui.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathFinderTest {

    private String 교대역_이름;
    private String 남부터미널역_이름;
    private String 강남역_이름;
    private String 양재역_이름;
    private Station 교대역;
    private Station 남부터미널역;
    private Station 강남역;
    private Station 양재역;

    @BeforeEach
    void setUp() {
        교대역_이름 = "교대역";
        남부터미널역_이름 = "남부터미널역";
        강남역_이름 = "강남역";
        양재역_이름 = "양재역";

        교대역 = new Station(1L, 교대역_이름);
        남부터미널역 = new Station(2L, 남부터미널역_이름);
        강남역 = new Station(3L, 강남역_이름);
        양재역 = new Station(4L, 양재역_이름);

    }


    /**
     * ㅤ교대역 -------- *2호선* ----- 강남역 <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 10ㅤㅤㅤㅤㅤ| <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ|  <br>
     * *3호선*ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ*신분당선*  <br>
     * 거리: 2ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 10  <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ|  <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ|  <br>
     * 남부터미널역ㅤ----ㅤ*3호선*ㅤ----ㅤ양재역  <br>
     * ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 3  <br>
     */
    @Test
    void 최단경로_조회() {
        // given
        PathFinder pathFinder = new PathFinder();

        Line 삼호선 = new Line("3호선", "주황");
        삼호선.addSection(교대역, 남부터미널역, 3);
        삼호선.addSection(남부터미널역, 양재역, 3);

        Line 이호선 = new Line("2호선", "초록");
        이호선.addSection(교대역, 강남역, 10);

        Line 신분당선 = new Line("신분당선", "빨강");
        신분당선.addSection(강남역, 양재역, 10);

        // when
        PathResponse result = pathFinder.findShortestPath(교대역, 양재역);

        assertAll(() -> {
            assertThat(result.getDistance()).isEqualTo(6);
            assertThat(result.getStations().stream().map(s -> s.getName()).collect(Collectors.toList())).containsExactly(교대역_이름, 남부터미널역_이름, 양재역_이름);
        });
    }
}
