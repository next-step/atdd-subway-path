package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import nextstep.subway.domain.request.LineRequest;
import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.domain.response.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.utils.LineTestUtil.createSubwayLine;
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
        Map<String, String> params = new HashMap<>();
        params.put("source", 교대역.toString());
        params.put("target", 양재역.toString());

        PathResponse response = RestAssured.given().log().all()
                .queryParams(params)
                .when().get("/paths")
                .then().log().all()
                .extract()
                .as(PathResponse.class);

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
}
