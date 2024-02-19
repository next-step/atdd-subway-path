package nextstep.subway.acceptance;

import nextstep.subway.domain.request.LineRequest;
import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.domain.response.StationResponse;
import nextstep.subway.exception.ExceptionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ExceptionMessage.*;
import static nextstep.subway.utils.LineTestUtil.createSubwayLine;
import static nextstep.subway.utils.PathTestUtil.getShortestPath;
import static nextstep.subway.utils.SectionTestUtil.addSection;
import static nextstep.subway.utils.SectionTestUtil.createSectionParams;
import static nextstep.subway.utils.StationTestUtil.createStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 검색")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql("/truncate.sql")
public class PathAcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = createStation("교대역").jsonPath().getLong("id");
        강남역 = createStation("강남역").jsonPath().getLong("id");
        양재역 = createStation("양재역").jsonPath().getLong("id");
        남부터미널역 = createStation("남부터미널역").jsonPath().getLong("id");

        이호선 = createSubwayLine(new LineRequest("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = createSubwayLine(new LineRequest("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = createSubwayLine(new LineRequest("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        addSection(createSectionParams(남부터미널역, 양재역, 3), 삼호선);
    }

    /**
     * 교대역    --- *2호선, 10* ---   강남역
     *   |                            |
     * *3호선, 2*                   *신분당선, 10*
     *   |                            |
     * 남부터미널역  --- *3호선, 3* ---   양재
     */
    @DisplayName("지하철 최단 경로 탐색")
    @Test
    void findShortestPath() {
        // given (setUp)

        // when 교대역 ~ 양재 최단경로 구하기
        PathResponse response = getShortestPath(교대역, 양재역).as(PathResponse.class);

        // then
        List<StationResponse> stationList = response.getStationList();
        int pathDistance = response.getDistance();

        // 교대 - 남부터미널 - 양재 (거리 : 5)
        List<Long> stationIdList = stationList.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(stationIdList).hasSize(3),
                () -> assertThat(pathDistance).isEqualTo(5),
                () -> assertThat(stationIdList).startsWith(교대역),
                () -> assertThat(stationIdList).endsWith(양재역),
                () -> assertThat(stationIdList).contains(남부터미널역)
        );
    }

    @DisplayName("출발역과 도착역이 같으면 예외처리")
    @Test
    void findShortestPathException1() {
        // given (setUp)

        // when 교대역 ~ 교대역 (동일한 출발역, 도착역)
        String exceptionMessage = getShortestPath(교대역, 교대역).as(ExceptionResponse.class).getMessage();

        // then
        assertThat(exceptionMessage).isEqualTo(SAME_SOURCE_TARGET_EXCEPTION.getMessage());
    }

    /**
     * 교대역    --- *2호선, 10* ---   강남역
     *   |                            |
     * *3호선, 2*                   *신분당선, 10*
     *   |                            |
     * 남부터미널역  --- *3호선, 3* ---   양재
     *
     * 사당역 --- *4호선, 10* --- 이수역
     */
    @DisplayName("출발역과 도착역이 연결되어 있지 않으면 예외처리")
    @Test
    void findShortestPathException2() {
        // given (setUp)
        // 4호선 (사당역 - 이수역) 추가
        Long 사당역 = createStation("사당역").jsonPath().getLong("id");
        Long 이수역 = createStation("이수역").jsonPath().getLong("id");
        Long 사호선 = createSubwayLine(new LineRequest("4호선", "orange", 사당역, 이수역, 2)).jsonPath().getLong("id");

        // when 교대역 ~ 사당역
        String exceptionMessage = getShortestPath(교대역, 사당역).as(ExceptionResponse.class).getMessage();

        // then
        assertThat(exceptionMessage).isEqualTo(NOT_CONNECTED_EXCEPTION.getMessage());
    }

    /**
     * 교대역    --- *2호선, 10* ---   강남역
     *   |                            |
     * *3호선, 2*                   *신분당선, 10*
     *   |                            |
     * 남부터미널역  --- *3호선, 3* ---   양재
     */
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 예외처리")
    @Test
    void findShortestPathException3() {
        // given (setUp)
        // 노선에 존재하지 않는 사당역
        Long 사당역 = createStation("사당역").jsonPath().getLong("id");

        // when 교대역 ~ 사당역
        String exceptionMessage = getShortestPath(교대역, 사당역).as(ExceptionResponse.class).getMessage();

        // then
        assertThat(exceptionMessage).isEqualTo(NO_EXISTS_STATION_EXCEPTION.getMessage());
    }

}
