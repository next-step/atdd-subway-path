package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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

        Map<String, Object> lineCreateParams = createLineCreateParams(강남역, 양재역, 10);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면<br>
     * Then 노선에 새로운 구간이 추가된다<br>
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Nested
    class 지하철_노선에_구간을_등록 {

        /**
         * (기존 노선) 강남역 -10- 양재역<br>
         * (추가할 노선) 신논현역 -3- 강남역<br>
         * 신논현역 -3- 강남역 -10- 양재역<br>
         */
        @DisplayName("상행_종점으로_등록")
        @Test
        void 상행_종점으로_등록() {
            Long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, 3));

            // then
            노선에_역들이_포함되어_있다(신분당선, List.of(신논현역, 강남역, 양재역));
        }

        /**
         * (기존 노선) 강남역 -10- 양재역<br>
         * (추가할 노선) 양재역 -6- 정자역<br>
         * 강남역 -10- 양재역 -6- 정자역<br>
         */
        @DisplayName("하행_종점으로_등록")
        @Test
        void 하행_종점으로_등록() {
            // when
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

            // then
            노선에_역들이_포함되어_있다(신분당선, List.of(강남역, 양재역, 정자역));
        }

        /**
         * (기존 노선) 강남역 -10- 양재역 -6- 정자역<br>
         * (추가할 노선) 양재역 - 3 - 양재시민의숲<br>
         * 강남역 -10- 양재역 -3- 양재시민의숲 -3- 정자역<br>
         */
        @DisplayName("역_사이_새로운_역을_등록")
        @Test
        void 역_사이_새로운_역을_등록() {
            // given
            Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, 6));

            // when
            Long 양재시민의숲 = 지하철역_생성_요청("양재시민의숲").jsonPath().getLong("id");
            지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 양재시민의숲, 3));

            // then
            노선에_역들이_포함되어_있다(신분당선, List.of(강남역, 양재역, 정자역, 양재시민의숲));
        }

        private void 노선에_역들이_포함되어_있다(Long 노선, List<Long> 역들) {
            ExtractableResponse<Response> response = 지하철_노선_조회_요청(노선);
            assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsAll(역들)
            );
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

    private Map<String, Object> createLineCreateParams(Long upStationId, Long downStationId, int distance) {
        return Map.of(
            "name", "신분당선",
            "color", "bg-red-600",
            "upStationId", upStationId,
            "downStationId", downStationId,
            "distance", distance
        );
    }

    private Map<String, Object> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        return Map.of(
            "upStationId", upStationId,
            "downStationId", downStationId,
            "distance", distance
        );
    }
}
