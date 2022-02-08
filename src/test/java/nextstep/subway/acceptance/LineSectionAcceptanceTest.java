package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 신도림역;
    private Long 문래역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");
        문래역 = 지하철역_생성_요청("문래역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 강남->양재 구간에 강남->정자 구간 추가를 요청 하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 강남->정자, 정자->양재 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 왼쪽과 중간에 구간을 등록")
    @Test
    void addLeftAndMiddleSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("강남역", "정자역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("정자역", "양재역");
    }

    /**
     * When 지하철 노선에 강남->양재 구간에 정자->양재 구간 추가를 요청 하면
     * Then
     * 1. 200 status code 가 반환된다.
     * 2. 강남->양재, 정자->양재 새로운 구간이 추가된다
     */
    @DisplayName("기존 구간의 중간과 오른쪽에 구간을 등록")
    @Test
    void addMiddleAndRightSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 5));
        // then
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
    void addRightSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("강남역", "양재역");
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
    void addLeftSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("sections.downStation.name", String.class)).containsExactly("정자역", "강남역");
        assertThat(response.jsonPath().getList("sections.upStation.name", String.class)).containsExactly("강남역", "양재역");
    }

    /**
     * When 강남역 -> 양재역 -> 미금역 구간에 강남역 삭제 요청을 하면
     * 1. status code 204를 반환하고,
     * 2. 양재역 -> 미금역이 된다.
     */
    @DisplayName("가장 왼쪽에 존재하는 역을 삭제 요청")
    @Test
    void deleteLeftSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
    }


    /**
     * When 강남역 -> 양재역 -> 미금역 구간에 강남역 삭제 요청을 하면
     * 1. status code 204를 반환하고,
     * 2. 강남역 -> 양재역이 된다.
     */
    @DisplayName("가장 오른쪽에 존재하는 역을 삭제 요청")
    @Test
    void deleteRightSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    /**
     * When 강남역 -> 양재역 -> 미금역 구간에 양재역 삭제 요청을 하면
     * 1. status code 204를 반환하고,
     * 2. 강남역 -> 양재역이 된다.
     * 3. 거리는 강남역 -> 양재역 + 양재역- > 미금역 이 된다.
     */
    @DisplayName("중간에 존재하는 역을 삭제 요청")
    @Test
    void deleteMiddleSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        assertThat(response.jsonPath().getList("sections.distance", Integer.class)).containsExactly(15);
    }

    /**
     * When 강남역 -> 양재역 -> 미금역 구간에 신도림역 삭제 요청을 하면
     * 1. status code 400을 반환한다
     */
    @DisplayName("노선에 등록되어있지 않은 역을 제거 요청")
    @Test
    void notExistedStationDeleteException() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 5));
        // then
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 신도림역);
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
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신도림역, 문래역, 5));
        // then
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
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 5));
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> createLineCreateParams(Long downStationId, Long upStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("distance", distance + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long downStationId, Long upStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("downStationId", downStationId + "");
        params.put("upStationId", upStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}