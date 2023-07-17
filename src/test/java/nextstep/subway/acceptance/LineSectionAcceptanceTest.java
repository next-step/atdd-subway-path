package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    private Long 정자역;
    private Long 신사역;

    private final int 거리_강남역_양재역 = 10;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    @Nested
    @DisplayName("구간 등록 테스트")
    public class SectionCreationTest {

        /**
         * Given 강남역-양재역 지하철 노선(거리 10)에
         * When 양재역-정자역(거리 6) 구간 추가를 요청 하면
         * Then 노선에 새로운 구간이 추가되고 (강남역-양재역-정자역)
         * Then 강남역-양재역은 거리가 10, 양재역-정자역은 거리가 6이다.
         */
        @DisplayName("지하철 노선의 마지막에 구간을 추가")
        @Test
        void addLineSection_atTheEnd() {

            // given
            int 거리_양재역_정자역 = 6;

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 거리_양재역_정자역));

            // then
            응답코드_검증(response, HttpStatus.OK);
            노선에_포함된_역_검증(신분당선, 강남역, 양재역, 정자역);
            거리_검증(강남역, 양재역, 거리_강남역_양재역);
            거리_검증(양재역, 정자역, 거리_양재역_정자역);
        }

        /**
         * Given 강남역-양재역 지하철 노선(거리 10)에
         * When 강남역-정자역(거리 6) 구간 추가를 요청 하면
         * Then 노선에 새로운 구간이 추가되고 (강남역-정자역-양재역)
         * Then 강남역-정자역은 거리가 6, 양재역-정자역은 거리가 4이다.
         */
        @DisplayName("지하철 노선의 중간에 구간을 추가 (상행역 기준)")
        @Test
        void addLineSection_atTheMiddle_basedOnUpStation() {

            // given
            int 거리_강남역_정자역 = 6;
            int 거리_정자역_양재역 = 4;

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 거리_강남역_정자역));

            // then
            응답코드_검증(response, HttpStatus.OK);
            노선에_포함된_역_검증(신분당선, 강남역, 정자역, 양재역);

            거리_검증(강남역, 정자역, 거리_강남역_정자역);
            거리_검증(정자역, 양재역, 거리_정자역_양재역);
        }

        /**
         * Given 강남역-양재역 지하철 노선(거리 10)에
         * When 정자역-양재역(거리 6) 구간 추가를 요청 하면
         * Then 노선에 새로운 구간이 추가되고 (강남역-정자역-양재역)
         * Then 강남역-정자역은 거리가 4, 양재역-정자역은 거리가 6이다.
         */
        @DisplayName("지하철 노선의 중간에 구간을 추가 (하행역 기준)")
        @Test
        void addLineSection_atTheMiddle_basedOnDownStation() {

            // given
            int 거리_강남역_정자역 = 4;
            int 거리_정자역_양재역 = 6;

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 거리_정자역_양재역));

            // then
            응답코드_검증(response, HttpStatus.OK);
            노선에_포함된_역_검증(신분당선, 강남역, 정자역, 양재역);

            거리_검증(강남역, 정자역, 거리_강남역_정자역);
            거리_검증(정자역, 양재역, 거리_정자역_양재역);

        }

        /**
         * Given 강남역-양재역 지하철 노선(거리 10)에
         * When 정자역-강남역(거리 6) 구간 추가를 요청 하면
         * Then 노선에 새로운 구간이 추가되고 (정자역-강남역-양재역)
         * Then 정자역-강남역은 거리가 6, 강남역-양재역은 거리가 10이다.
         */
        @DisplayName("지하철 노선의 시작점에 구간을 추가")
        @Test
        void addLineSection_atTheStart() {

            // given
            int 거리_정자역_강남역 = 6;
            int 거리_강남역_양재역 = 10;

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역, 거리_정자역_강남역));

            // then
            응답코드_검증(response, HttpStatus.OK);
            노선에_포함된_역_검증(신분당선, 정자역, 강남역, 양재역);

            거리_검증(정자역, 강남역, 거리_정자역_강남역);
            거리_검증(강남역, 양재역, 거리_강남역_양재역);
        }

        /**
         * Given 강남역-양재역 지하철 노선에
         * When 정자역-신사역 구간 추가를 요청 하면
         * Then 오류가 발생한다.
         */
        @DisplayName("[오류] 노선에 존재하지 않는 역으로 이루어진 구간 추가")
        @Test
        void addLineSection_error_nonExistStation() {

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 신사역, 3));

            // then
            응답코드_검증(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Given 강남역-양재역 지하철 노선에
         * When 강남역-양재역 구간 추가를 요청 하면
         * Then 오류가 발생한다.
         */
        @DisplayName("[오류] 노선에 존재하는 역으로 이루어진 구간 추가")
        @Test
        void addLineSection_error_allExistStation() {

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));

            // then
            응답코드_검증(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Given 강남역-양재역 지하철 노선(거리 10)에
         * When 강남역-정자역(거리 11) 구간 추가를 요청 하면
         * Then 오류가 발생한다.
         */
        @DisplayName("[오류] 지하철 노선의 중간에 거리가 긴 구간을 추가 (상행역 기준)")
        @Test
        void addLineSection_error_distanceTooLong_atTheMiddle_basedOnUpStation() {

            // given
            int 거리_강남역_정자역 = 11;

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 거리_강남역_정자역));

            // then
            응답코드_검증(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        /**
         * Given 강남역-양재역 지하철 노선(거리 10)에
         * When 정자역-양재역(거리 10) 구간 추가를 요청 하면
         * Then 오류가 발생한다.
         */
        @DisplayName("[오류] 지하철 노선의 중간에 거리가 긴 구간을 추가 (하행역 기준)")
        @Test
        void addLineSection_error_distanceTooLong_atTheMiddle_basedOnDownStation() {

            // given
            int 거리_정자역_양재역 = 10;

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 거리_정자역_양재역));

            // then
            응답코드_검증(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Given 강남역-양재역 지하철 노선(거리 10)에
         * When 정자역-양재역(거리 0) 구간 추가를 요청 하면
         * Then 오류가 발생한다.
         */
        @DisplayName("[오류] 지하철 노선의 중간에 거리가 0인 구간을 추가")
        @Test
        void addLineSection_error_zeroDistance_atTheMiddle_basedOnDownStation() {

            // given
            int 거리_정자역_양재역 = 0;

            // when
            ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 양재역, 거리_정자역_양재역));

            // then
            응답코드_검증(response, HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역);
    }



    /**
     * Given 강남역-양재역 지하철 노선에
     * When 강남역-양재역 구간을 조회하면
     * Then 구간이 조회가 된다.
     */
    @DisplayName("지하철 노선 구간을 조회")
    @Test
    void findLineSection() {

        // when
        ExtractableResponse<Response> response = 지하철_노선의_구간정보_조회(신분당선, 강남역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(거리_강남역_양재역);
        assertThat(response.jsonPath().getLong("upStationId")).isEqualTo(강남역);
        assertThat(response.jsonPath().getLong("downStationId")).isEqualTo(양재역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 거리_강남역_양재역 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }



    private static void 응답코드_검증(ExtractableResponse<Response> response, HttpStatus httpStatus) {
        Assertions.assertThat(response.statusCode()).isEqualTo(httpStatus.value());
    }

    private void 노선에_포함된_역_검증(Long 노선_id, Long... 노선_목록) {
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(노선_id);
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(노선_목록);
    }

    private void 거리_검증(Long 양재역, Long 정자역, int 거리_양재역_정자역) {
        ExtractableResponse<Response> sectionResponse = 지하철_노선의_구간정보_조회(신분당선, 양재역, 정자역);
        assertThat(sectionResponse.jsonPath().getInt("distance")).isEqualTo(거리_양재역_정자역);
    }
}
