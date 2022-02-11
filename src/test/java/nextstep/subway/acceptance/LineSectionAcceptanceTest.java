package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.LineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    @Autowired
    LineService lineService;

    private Long 신분당선;
    private Long 이호선;
    private Long 삼호선;

    private Long 강남역;
    private Long 양재역;
    private Long 교대역;
    private Long 남부터미널역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10).jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10).jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2).jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    /**
     * When 지하철 노선에 강남->양재 구간에 강남->정자 구간 추가를 요청 하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 강남->정자, 정자->양재 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 왼쪽과 중간에 구간을 등록")
    @Test
    void addDownSectionAtMiddle() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 5));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class))
                .containsExactly("강남역", "정자역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class))
                .containsExactly("정자역", "양재역");
    }

    /**
     * When 지하철 노선에 강남->양재 구간에 정자->양재 구간 추가를 요청 하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 강남->정자, 정자->양재 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 중간과 오른쪽에 구간을 등록")
    @Test
    void addUpSectionAtMiddle() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 5));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("강남역", "정자역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("정자역", "양재역");
    }

    /**
     * When 지하철 노선에 강남역->양재역 구간에 양재역->정자역 구간 추가를 요청 하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 강남역->양재역, 양재역->정자역 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 오른쪽에 구간을 등록")
    @Test
    void addUpMostSection() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("강남역",
                "양재역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("양재역", "정자역");
    }

    /**
     * When 지하철 노선에 강남역->양재역 구간에 정자역->강남역 구간 추가를 요청 하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 정자역->강남역, 강남역->양재역 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 왼쪽에 구간을 등록")
    @Test
    void addDownMostSection() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 5));

        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("정자역",
                "강남역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("강남역", "양재역");
    }

    /**
     * When 강남역 -> 양재역 -> 정자역 구간에 강남역 삭제 요청을 하면
     * Then
     * 1. status code 204를 반환하고,
     * 2. 양재역 -> 정자역이 된다.
     */
    @DisplayName("가장 왼쪽에 존재하는 역을 삭제 요청")
    @Test
    void deleteDownMostSection() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }

    /**
     * When 강남역 -> 양재역 -> 미금역 구간에 강남역 삭제 요청을 하면
     * Then
     * 1. status code 204를 반환하고,
     * 2. 강남역 -> 양재역이 된다.
     */
    @DisplayName("가장 오른쪽에 존재하는 역을 삭제 요청")
    @Test
    void deleteUpMostSection() {

        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 강남역 -> 양재역 -> 미금역 구간에 양재역 삭제 요청을 하면
     * Then
     * 1. status code 204를 반환하고,
     * 2. 강남역 -> 양재역이 된다.
     * 3. 거리는 강남역 -> 양재역 + 양재역- > 미금역 이 된다.
     */
    @DisplayName("중간에 존재하는 역을 삭제 요청")
    @Test
    void deleteMiddleSection() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        assertThat(response.jsonPath().getList("sections.distance", Integer.class)).containsExactly(15);
    }

    /**
     * When 강남역 -> 양재역 -> 미금역 구간에 신도림역 삭제 요청을 하면
     * Then
     * 1. status code 400을 반환한다
     */
    @DisplayName("노선에 등록되어있지 않은 역을 제거 요청")
    @Test
    void notExistedStationDeleteException() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 교대역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 강남역->양재역 구간에 신도림역->문래역 구간 추가를 요청 하면
     * Then
     * 1. 400 status code 가 반환된다.
     * 2. 구간 생성이 실패한다.
     */
    @DisplayName("노선에 존재하지 않는 상행역과 하행역을 가지는 구간 생성 요청")
    @Test
    void NoMatchSectionException() {
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                createSectionCreateParams(교대역, 남부터미널역, 5));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 지하철 노선에 강남역->양재역 구간에 강남역->양재역 구간 추가를 요청 하면
     * Then
     * 1. 400 status code 가 반환된다.
     * 2. 구간 생성이 실패한다.
     */
    @DisplayName("노선에 이미 존재하는 상행역과 하행역을 가지는 구간 생성 요청")
    @Test
    void AllMatchSectionException() {
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                createSectionCreateParams(강남역, 양재역, 5));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 양재역의 최단거리를 구하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 지하철역들과 최단거리가 반환된다
     */
    @Test
    void path() {
        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,3L);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
        assertThat(response.jsonPath().getList("stations.id")).containsExactly(1,4,3);

    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 교대역의 최단거리를 구하면
     * Then 400 status code 가 반환된다.
     */
    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    void cantGetPathBySameStationException() {
        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,1L);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 교대역의 최단거리를 구하면
     * Then 400 status code 가 반환된다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void unConnectedSourceAndTargetException() {
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,5L);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    /**
     * 교대역(1)    --- *2호선* ---   강남역(2)
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역(4)  --- *3호선* ---   양재(3)
     */

    /**
     * given 위와 같은 지하철 역이 주어졌을 때
     * when 교대역과 없는역의 최단거리를 구하면
     * Then 400 status code 가 반환된다.
     */
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void NotExistedSourceOrTargetException() {
        ExtractableResponse<Response> response = 역과역_사이에_최단거리(1L,5L);
        assertThat(response.statusCode()).isEqualTo(400);
    }

    private Map<String, String> createSectionCreateParams(Long downStationId, Long upStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", downStationId + "");
        params.put("upStationId", upStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}