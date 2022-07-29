package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    @DisplayName("지하철 노선에 구간을 등록")
    @Nested
    class describe_addLineSection {
        /**
         * Given 새로운 역을 생성하고
         * When 새로운 역을 하행 종점으로 구간 추가를 요청 하면
         * Then 노선에 새로운 역이 하행 종점으로 등록된다.
         */
        @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
        @Test
        void action_성공1() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
        }

        /**
         * Given 새로운 역을 생성하고
         * When 새로운 역을 상행 종점으로 구간 추가를 요청 하면
         * Then 노선에 새로운 역이 상행 종점으로 등록된다.
         */
        @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
        @Test
        void action_성공2() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(정자역, 강남역, 양재역);
        }

        /**
         * Given 새로운 역을 생성하고
         * When 새로운 역을 중간역으로 구간 추가를 요청 하면
         * Then 노선 중간에 새로운 역이 등록된다.
         */
        @DisplayName("역 사이에 새로운 역을 등록할 경우")
        @Test
        void action_성공3() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 3));

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역, 양재역);
        }

        /**
         * When 기존 역을 새로운 구간으로 추가하면
         * Then 요청에 실패한다
         */
        @DisplayName("예외 1) 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
        @Test
        void action_실패1() {
            // when AND then
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * Given 새로운 역을 2개 생성하고
         * When 새로운 역으로만 구성된 구간을 추가하면
         * Then 요청에 실패한다
         */
        @DisplayName("예외 2) 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
        @Test
        void action_실패2() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            Long 판교역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

            // when AND then
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 판교역));
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * Given 새로운 역을 생성하고
         * When 기존 구간보다 큰 구간을 사이에 등록하면
         * Then 요청에 실패한다
         */
        @DisplayName("예외 3) 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
        @Test
        void action_실패3() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

            // when AND then
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 100));
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("지하철 노선에서 구간을 제거")
    @Nested
    class describe_removeLineSection {
        /**
         * Given 지하철 노선에 새로운 구간을 추가하고
         * When 지하철 노선의 하행 종점역 제거를 요청 하면
         * Then 노선에서 해당 역이 제거된다
         */
        @DisplayName("하행 종점을 제거하는 경우")
        @Test
        void action_성공1() {
            // given 강남-양재-정자 등록됨
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // when 정자역 삭제
            지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
        }

        /**
         * Given 지하철 노선에 새로운 구간을 추가하고
         * When 지하철 노선의 중간역 제거를 요청 하면
         * Then 노선에서 해당 역이 제거된다
         */
        @DisplayName("중간 역을 제거하는 경우")
        @Test
        void action_성공2() {
            // given 강남-양재-정자 등록됨
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // when 양재역 삭제
            지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 정자역);
        }

        /**
         * Given 지하철 노선에 새로운 구간을 추가하고
         * When 지하철 노선의 상행 종점역 제거를 요청 하면
         * Then 노선에서 해당 역이 제거된다
         */
        @DisplayName("상행 종점을 제거하는 경우")
        @Test
        void action_성공3() {
            // given 강남-양재-정자 등록됨
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

            // when 강남역 삭제
            지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

            // then
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(양재역, 정자역);
        }

        /**
         * Given 노선에 구간이 하나만 추가되고
         * When 지하철 노선의 상행역을 제거하면
         * Then 요청에 실패한다
         */
        @DisplayName("예외1) 구간이 하나 남은 경우에는 상행역을 제거할 수 없다.")
        @Test
        void action_실패1() {
            // given 강남-양재 등록됨

            // when 강남역 삭제
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

            // then 실패
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * Given 노선에 구간이 하나만 추가되고
         * When 지하철 노선의 하행역을 제거하면
         * Then 요청에 실패한다
         */
        @DisplayName("예외2) 구간이 하나 남은 경우에는 하행역을 제거할 수 없다.")
        @Test
        void action_실패2() {
            // given 강남-양재 등록됨

            // when 강남역 삭제
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

            // then 실패
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * Given 새로운 역을 추가하고
         * When 지하철 노선에 등록되지 않은 새로운 역을 제거하면
         * Then 요청에 실패한다
         */
        @DisplayName("예외3) 노선에 등록되지 않은 역은 제거할 수 없다.")
        @Test
        void action_실패3() {
            // given 강남-양재 등록됨, 정자역은 구간 추가되지 않음
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

            // when 정자역 삭제
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

            // then 실패
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        return createSectionCreateParams(upStationId, downStationId, 10);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
