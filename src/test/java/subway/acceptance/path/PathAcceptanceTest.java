package subway.acceptance.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import subway.acceptance.AcceptanceTest;
import subway.acceptance.line.LineRequestGenerator;
import subway.acceptance.line.LineSteps;
import subway.acceptance.line.SectionFixture;
import subway.acceptance.station.StationSteps;

import java.util.HashMap;
import java.util.Map;

@DisplayName("경로 관련 기능")
public class PathAcceptanceTest extends AcceptanceTest {
    private Map<String, Long> stationsMap = new HashMap<>();

    private String 일호선_URI;
    private String 삼호선_URI;
    private String 신분당선_URI;


    // TODO: 인수 테스트 작성

    /**
     * 교대역  ---- *2호선* --- d:10 ------  강남역
     * |                                    |
     * *3호선*                            *신분당선*
     * d:2                                 d:10
     * |                                   |
     * 남부터미널역  --- *3호선* -- d:3 --- 양재역
     */

    @BeforeEach
    void createLine() {
        stationsMap = StationSteps.기본_역_생성();

        var 이호선_요청 = LineRequestGenerator.generateLineCreateRequest("2호선", "bg-green-600", getStationId("강남역"), getStationId("교대역"), 10L);
        LineSteps.노선_생성_API(이호선_요청);

        var 삼호선_요청 = LineRequestGenerator.generateLineCreateRequest("3호선","bg-amber-600", getStationId("교대역"), getStationId("남부터미널역"),2L);
        var createResponse = LineSteps.노선_생성_API(삼호선_요청);
        final String createdLocation = createResponse.header("Location");
        final String appendLocation = createdLocation + "/sections";

        var 삼호선_끝에_구간_추가 = SectionFixture.구간_요청_만들기(getStationId("남부터미널역"), getStationId("양재역"), 3L);
        LineSteps.구간_추가_API(appendLocation, 삼호선_끝에_구간_추가);

        var 신분당선_요청 = LineRequestGenerator.generateLineCreateRequest("신분당선","bg-hotpink-600", getStationId("강남역"), getStationId("양재역"),10L);
        LineSteps.노선_생성_API(신분당선_요청);

        LineSteps.노선_목록_조회_API();
    }

    /**
     * Given 3개의 구간을 가진 노선이 있고
     * When 노선의 상행역과 하행역으로 경로를 조회하면
     * Then 4개의 역이 출력된다
     * Then 3 구간의 모든 거리의 합이 출력된다
     */
    @DisplayName("같은 노선의 경로를 조회한다")
    @Test
    void getPath() {
        // TODO : 여기서 부터 하면 됨
        UriComponents retrieveQueryWithBaseUri = UriComponentsBuilder
                .fromUriString("/path")
                .queryParam("source", getStationId("교대역"))
                .queryParam("target", getStationId("양재역"))
                .build();
        var response = RestAssured.given().log().all()
                .when().get(retrieveQueryWithBaseUri.toUri())
                .then().log().all()
                .extract()
                .response();

    }

    /**
     * Given 각 구간을 가진 3개의 서로 연결된 노선이 있고
     * When 3 노선을 모두 통과하는 경로를 조회하면
     * Then 경로 조회 결과가 나온다
     * Then 구간의 모든 거리의 합이 출력된다
     */
    @DisplayName("다른 노선에 있는 지하철 경로를 조회한다")
    @Test
    void getPathWithOtherLine() {

    }

    /**
     * Given 각 구간을 가진 3개의 서로 연결된 노선이 있고
     * When 연결되지 않은 경로를 조회하면
     * Then 경로가 조회되지 않는다
     */
    @DisplayName("연결되지 않은 경로를 조회한다")
    @Test
    void getPathWithNotConnected() {

    }

    /**
     * Given 각 구간을 가진 3개의 서로 연결된 노선이 있고
     * When 시작과 끝을 같은 역을 조회하면
     * Then 경로가 조회되지 않는다
     */
    @DisplayName("시작과 끝이 같은 역의 경로를 조회한다.")
    @Test
    void getPathWithSameStation() {

    }

    /**
     * Given 각 구간을 가진 3개의 서로 연결된 노선이 있고
     * When 존재 하지 않는 역으로 경로를 조회하면
     * Then 경로가 조회되지 않는다
     */
    @DisplayName("존재하지 않는 역으로 경로를 조회한다")
    @Test
    void getPathWithNotExistStation() {

    }

    private Long getStationId(String name) {
        return stationsMap.get(name);
    }


}
