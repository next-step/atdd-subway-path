package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.support.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.support.LineSteps.지하철_노선_생성_요청후_식별자_반환;
import static nextstep.subway.acceptance.support.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.support.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.support.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.support.StationSteps.지하철역_생성_요청후_식별자_반환;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;
    private Long 정자역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청후_식별자_반환("강남역");
        양재역 = 지하철역_생성_요청후_식별자_반환("양재역");
        정자역 = 지하철역_생성_요청후_식별자_반환("정자역");

        신분당선 = 지하철_노선_생성_요청후_식별자_반환(createLineCreateParams("신분당선", 강남역, 양재역, 10));
    }

    @Nested
    class 구간등록{
        /**
         * Given 지하철 노선에 구간을 생성하고
         * When 지하철 구간 사이에 새로운 구간 추가 요청 하면
         * Then 노선에 새로운 구간이 추가된다
         */
        @Test
        void 지하철구간_사이에_새로운구간_추가요청() {
            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 정자역, 6));

            // then
            노선_조회후_지하철역_포함_확인(강남역, 정자역, 양재역);
        }

        /**
         * Given 지하철 노선에 구간을 생성하고
         * When 새로운 구간을 기존 지하철 노선의 상행 종점으로 추가 요청 하면
         * Then 노선에 새로운 구간이 추가된다
         */
        @Test
        void 새로운구간을_기존지하철_노선의_상행종점으로_추가요청(){
            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 강남역,6));

            // then
            노선_조회후_지하철역_포함_확인(정자역, 강남역, 양재역);
        }

        /**
         * Given 지하철 노선에 새로운 구간을 생성하고
         * When 새로운 구간을 기존 지하철 노선의 하행 종점으로 추가 요청 하면
         * Then 노선에 새로운 구간이 추가된다
         */
        @Test
        void 새로운구간을_기존지하철_노선의_하행종점으로_추가요청(){
            // when
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

            // then
            노선_조회후_지하철역_포함_확인(강남역, 양재역, 정자역);
        }
    }

    @Nested
    class RemoveSection {
        /**
         * Given 지하철 노선에 새로운 구간을 생성하고
         * When 종점을 제거하면
         * Then 노선에 구간이 제거된다
         */
        @Test
        void 상행_종점_제거() {
            // given
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

            // when
            지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

            // then
            노선_조회후_지하철역_포함_확인(양재역, 정자역);
        }

        /**
         * Given 지하철 노선에 새로운 구간을 생성하고
         * When 종점을 제거하면
         * Then 노선에 구간이 제거된다
         */
        @Test
        void 하행_종점_제거() {
            // given
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

            // when
            지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

            // then
            노선_조회후_지하철역_포함_확인(강남역, 양재역);
        }

        /**
         * Given 지하철 노선에 새로운 구간을 생성하고
         * When 중간역을 제거하면
         * Then 노선에 구간이 제거된다
         */
        @Test
        void 중간역_제거() {
            // given
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

            // when
            지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

            // then
            노선_조회후_지하철역_포함_확인(강남역, 정자역);
        }

        /**
         * Given 지하철 노선에 새로운 구간을 생성하고
         * When 구간이 하나인 노선에서 마지막 구간을 제거하면
         * Then 노선에 구간이 제거가 안된다.
         */
        @Test
        void 구간이_하나인_노선에서_마지막구간_제거() {
            // when
            지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);

            // then
            노선_조회후_지하철역_포함_확인(강남역, 양재역);
        }
    }


    private void 노선_조회후_지하철역_포함_확인(Long... stationId) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(stationId);
    }

    private Map<String, String> createLineCreateParams(String name, Long upStationId, Long downStationId, int distance) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", name);
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", distance + "");
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
