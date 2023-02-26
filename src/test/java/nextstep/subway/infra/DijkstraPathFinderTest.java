package nextstep.subway.infra;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DijkstraPathFinderTest {

    private String 교대역_이름;
    private String 남부터미널역_이름;
    private String 강남역_이름;
    private String 양재역_이름;
    private String 판교역_이름;
    private Station 교대역;
    private Station 남부터미널역;
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;
    private Line 삼호선;
    private Line 이호선;
    private Line 신분당선;
    private Line 신신분당선;

    /**
     * ㅤ교대역 -------- *2호선* ----- 강남역 --- *신신분당선* --- 판교역 <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 10ㅤㅤㅤㅤㅤ| <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ|  <br>
     * *3호선*ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ*신분당선*  <br>
     * 거리: 2ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 10  <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ|  <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ|  <br>
     * 남부터미널역ㅤ----ㅤ*3호선*ㅤ----ㅤ양재역  <br>
     * ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 3  <br>
     */
    @BeforeEach
    void setUp() {
        교대역_이름 = "교대역";
        남부터미널역_이름 = "남부터미널역";
        강남역_이름 = "강남역";
        양재역_이름 = "양재역";
        판교역_이름 = "판교역";

        교대역 = new Station(1L, 교대역_이름);
        남부터미널역 = new Station(2L, 남부터미널역_이름);
        강남역 = new Station(3L, 강남역_이름);
        양재역 = new Station(4L, 양재역_이름);
        판교역 = new Station(5L, 판교역_이름);

        삼호선 = new Line("3호선", "주황");
        삼호선.addSection(교대역, 남부터미널역, 3);
        삼호선.addSection(남부터미널역, 양재역, 3);

        이호선 = new Line("2호선", "초록");
        이호선.addSection(교대역, 강남역, 10);

        신분당선 = new Line("신분당선", "빨강");
        신분당선.addSection(강남역, 양재역, 10);

        신신분당선 = new Line("신신분당선", "찐빨강");
        신신분당선.addSection(강남역, 판교역, 40);
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
        PathFinder pathFinder = DijkstraPathFinder.create(List.of(삼호선, 이호선, 신분당선));

        // when
        Path result = pathFinder.findShortestPath(교대역, 양재역);

        assertAll(() -> {
            assertThat(result.getDistance()).isEqualTo(Distance.of(6));
            assertThat(result.getStations().stream().map(s -> s.getName()).collect(Collectors.toList())).containsExactly(교대역_이름, 남부터미널역_이름, 양재역_이름);
        });
    }


    /**
     * ㅤ강남역 --- *신신분당선* --- 판교역 <br>
     * ㅤㅤㅤㅤㅤㅤㅤㅤ 거리: 40ㅤㅤㅤㅤ<br>
     */
    @Test
    void 출발역과_도착역이_같은_경우_예외가_발생() {
        // given
        PathFinder pathFinder = DijkstraPathFinder.create(List.of(신신분당선));

        // when then
        assertThatThrownBy(() -> pathFinder.findShortestPath(강남역, 강남역)).isInstanceOf(SameStationPathException.class);
    }

    /**
     * ㅤ교대역ㅤㅤㅤㅤㅤㅤㅤㅤㅤ강남역ㅤ--- *신신분당선* --- 판교역 <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리:40 <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * *3호선*ㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * 거리: 2ㅤㅤㅤㅤㅤㅤㅤㅤ<br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * 남부터미널역ㅤ----ㅤ*3호선*ㅤ----ㅤ양재역  <br>
     * ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 3  <br>
     */
    @Test
    void 출발역과_도착역이_연결이_되어_있지_않은_경우_예외가_발생() {
        // given
        PathFinder pathFinder = DijkstraPathFinder.create(List.of(삼호선, 신신분당선));

        // when then
        assertThatThrownBy(() -> pathFinder.findShortestPath(교대역, 판교역)).isInstanceOf(NotExistsPathException.class);
    }

    /**
     * ㅤ교대역ㅤㅤㅤㅤㅤㅤㅤ <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤ <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * *3호선*ㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * 거리: 2ㅤㅤㅤㅤㅤㅤㅤㅤ<br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * ㅤㅤ|ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ  <br>
     * 남부터미널역ㅤ----ㅤ*3호선*ㅤ----ㅤ양재역  <br>
     * ㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤㅤ거리: 3  <br>
     */
    @Test
    void 존재하지_않은_출발역이나_도착역을_조회_할_경우_예외가_발생() {
        // given
        PathFinder pathFinder = DijkstraPathFinder.create(List.of(삼호선));

        // when then
        assertThatThrownBy(() -> pathFinder.findShortestPath(교대역, 판교역)).isInstanceOf(CanNotFindStationInPathException.class);
    }

}
