package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 정자역;
    private Long 판교역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
    }

    /**
     * Given 노선에 구간이 존재할 때
     * When 지하철 노선에 새로운 상행 구간 추가 요청을 하면
     * Then 노선에 새로운 상행 구간이 추가된다
     */
    @DisplayName("상행 구간 등록")
    @Test
    void addUpSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 10));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
    }

    /**
     * Given 노선에 구간이 존재할 때
     * When 지하철 노선의 중간에 구간 추가 요청을 하면
     * Then 노선의 중간에 새로운 구간이 추가된다
     */
    @DisplayName("사이 구간 등록")
    @Test
    void addMiddleSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 5));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
    }

    @DisplayName("중복된 구간 등록시 등록 실패")
    @Test
    void addSectionWithDuplicated() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 5));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("등록할 수 없는 구간길이 등록시 등록 실패")
    @Test
    void addSectionWithWrongDistance() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 11));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역 또는 하행역에 아무것도 포함되어있지 않으면 등록 실패")
    @Test
    void addSectionWithNotIncludeStation() {
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 11));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @Disabled
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 10));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }

    // TODO : 지하철 노선 제거 (새로운 요건)
    //  1. 기존에는 마지막 역 삭제만 가능했는데 위치에 상관 없이 삭제가 가능하도록 수정
    //  2. 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
    //  3. 중간역이 제거될 경우 재배치를 함
    //  4. 구간이 하나이면 제거 할 수 없음.

    @DisplayName("가장 상위의 상행역 제거")
    @Test
    void removeFirstLineSection() {
        // A - B - C - D
        // A - B, B - C, C - D
        // remove A
        // => B - C, C - D

        // TODO
        //  A 를 상행역으로 갖는 구간을 찾자.
        //  찾아서 지우고
        //  찾은게 첫번째 구간이면, 첫번째 구간만 지우면 끝

    }

    @DisplayName("중간에 있는 역 제거")
    @Test
    void removeMiddleLineSection() {
        // A - B - C - D
        // A - B, B - C, C - D
        // remove B
        // => A - C, C - D

        // TODO
        //  B 를 상행역으로 갖는 구간을 찾자.
        //  B 를 하행역으로 갖는 구간을 찾자.
        //  상행역으로 갖는 구간(1)의 상행역과
        //  하행역으로 갖는 구간(2)의 하행역을 새로운 구간으로 만들고, 이때 거리는 (1).거리 + (2).거리
        //  (1)과 (2) 구간은 삭제한다.
    }

    @DisplayName("마지막 하행역 제거")
    @Test
    void removeLastLineSection() {
        // A - B - C - D
        // A - B, B - C, C - D
        // remove D
        // => A - B, B - C

        // TODO
        //  D 를 상행역으로 갖는 구간을 찾자.
        //  D 를 하행역으로 갖는 구간을 찾자.
        //  D 를 하행역으로 가진 구간을 찾고
        //  삭제한다.
    }

    @DisplayName("구간이 하나일때는 제거할 수 없음")
    @Test
    void removeOnlyOneLineSection_then_exception() {
        // A - B
        // => exception
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
