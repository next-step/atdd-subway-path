package nextstep.subway.unit;


import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.domain.*;
import nextstep.subway.utils.LineTestFixtures;
import nextstep.subway.utils.StationTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static nextstep.subway.utils.LineTestFixtures.노선_생성_WITH_ID;
import static nextstep.subway.utils.StationTestFixtures.지하철역_생성;
import static nextstep.subway.utils.StationTestFixtures.지하철역_생성_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PathServiceTest {

    @Autowired
    private PathService pathService;

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;


    private Station 교대역;
    private Station 강남역;
    private Station 남부터미널역;
    private Station 양재역;

    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    /**
     * 교대역      ---     이호선      ---      강남역
     * |                                        |
     * 삼호선                                 신분당선
     * |                                        |
     * 남부터미널역  ---    삼호선      ---      양재역
     *
     */

    @BeforeEach
    void setup() {
        교대역 = 지하철역_저장("교대역");
        강남역 = 지하철역_저장("강남역");
        남부터미널역 = 지하철역_저장("남부터미널역");
        양재역 = 지하철역_저장("양재역");

        이호선 = 노선_저장("이호선", "green", 교대역, 강남역, 10);
        신분당선 = 노선_저장("신분당선", "red", 강남역, 양재역, 10);
        삼호선 = 노선_저장("삼호선", "orange", 교대역, 남부터미널역, 2);

        삼호선.addSection(남부터미널역, 양재역, 3);
    }


    private Station 지하철역_저장(final String stationName) {
        final Station 지하철역 = StationTestFixtures.지하철역_생성(stationName);
        return stationRepository.save(지하철역);
    }

    private Line 노선_저장(final String 노선명, final String 노선색, final Station 상행역, final Station 하행역, final int 거리) {
        return lineRepository.save(LineTestFixtures.노선_생성(노선명, 노선색, 상행역, 하행역, 거리));
    }

    @DisplayName("출발역에서 도착역까지의 지하철역 경로 조회")
    @Test
    void findRoutes() {

        // when
        ShortestRoute shortestRoute = pathService.findRoutes(교대역.getId(), 양재역.getId());

        // then
        assertThat(shortestRoute.routes()).containsExactly(교대역, 남부터미널역, 양재역);
        assertThat(shortestRoute.distance()).isEqualTo(5.0);
    }

    @DisplayName("출발역과 도착역이 같은 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfSourceAndTargetEquals() {

        // then
        assertThatThrownBy(() -> pathService.findRoutes(교대역.getId(), 교대역.getId()))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("이어지지 않은 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfNotConnectedStations() {

        // given
        final Station 구로역 = 지하철역_저장("구로역");
        final Station 신도림역 = 지하철역_저장("신도림역");
        final Line 일호선 = 노선_저장("일호선", "blue", 구로역, 신도림역, 5);

        assertThatThrownBy(() -> pathService.findRoutes(교대역.getId(), 신도림역.getId()))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("존재하지 않은 출발역을 기점으로 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfNotExistSource() {

        // given
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        // then
        assertThatThrownBy(() -> pathService.findRoutes(구로디지털단지역.getId(), 양재역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않은 도착역을 기점으로 경로를 조회")
    @Test
    void throwsExceptionFindRoutesIfNotExistTarget() {

        // given
        final Station 구로디지털단지역 = 지하철역_저장("구로디지털단지역");

        // then
        assertThatThrownBy(() -> pathService.findRoutes(교대역.getId(), 구로디지털단지역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
