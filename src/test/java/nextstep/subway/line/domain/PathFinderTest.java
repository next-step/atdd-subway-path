package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class PathFinderTest {

    private Station 강남역;
    private Station 양재역;
    private Station 선릉역;
    private Station 도곡역;
    private Station 역삼역;
    private Station 매봉역;
    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;
    private Line 분당선;

    /**
     *   강남역 ---*2호선* --- 역삼역 ---*2호선* --- 선릉역
     *     |                                         |
     * *신분당선*                                  *분당선*
     *    |                                         |
     *  양재역 ---*3호선*--- 매봉역  ---*3호선*---  도곡역
     */
    @BeforeEach
    void setUp() {
        // given
        강남역 = initStation(강남역, "강남역", 1L);
        양재역 = initStation(양재역,"양재역", 2L);
        선릉역 = initStation(선릉역,"선릉역", 3L);
        도곡역 = initStation(도곡역,"도곡역", 4L);
        역삼역 = initStation(역삼역,"역삼역", 5L);
        매봉역 = initStation(매봉역,"매봉역", 5L);

        이호선 = new Line("2호선", "green", 강남역, 선릉역, 20);
        삼호선 = new Line("3호선", "orange", 양재역, 도곡역, 20);
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, 7);
        분당선 = new Line("분당선", "yellow", 선릉역, 도곡역, 12);

        이호선.addSection(강남역, 역삼역, 10);
        삼호선.addSection(양재역, 매봉역, 10);
    }

    @DisplayName("최단 경로 정보를 가져온다.")
    @Test
    void getPathInfo() {
        // given
        PathFinder pathFinder = new PathFinder(asList(이호선, 삼호선, 신분당선, 분당선));

        // when
        StationGraph pathInfo = pathFinder.getPathInfo(역삼역, 도곡역);

        // then
        StationResponse 역삼역리스폰스 = StationResponse.of(역삼역);
        StationResponse 선릉역리스폰스 = StationResponse.of(선릉역);
        StationResponse 도곡역리스폰스 = StationResponse.of(도곡역);

        assertThat(pathInfo.getStations()).containsExactly(Arrays.array(역삼역리스폰스, 선릉역리스폰스, 도곡역리스폰스));
        assertThat(pathInfo.getIntegerWeight()).isEqualTo(22);
    }

    private Station initStation(Station station, String stationName, Long id) {
        station = new Station(stationName);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}