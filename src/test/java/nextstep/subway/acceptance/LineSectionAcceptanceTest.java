package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.신규_지하철역;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

        강남역 = 신규_지하철역("강남역");
        양재역 = 신규_지하철역("양재역");

        신분당선 = 지하철_노선_생성_요청(신규_라인(강남역, 양재역)).jsonPath().getLong("id");
    }

    @Nested
    @DisplayName("구간 등록")
    class addSection {

        @Nested
        @DisplayName("성공")
        class success {
            /**
             * When 지하철 노선에 새로운 구간 추가를 요청 하면
             * Then 노선에 새로운 구간이 추가된다
             */
            @DisplayName("지하철 노선에 구간을 등록")
            @Test
            void addLineSection() {
                // when
                Long 정자역 = 신규_지하철역("정자역");
                지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역, 6L));

                // then
                ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역);
            }

            /**
             * When 지하철 노선의 기존의 구간에 구간 추가를 요청 하면
             * Then 구간 사이에 새로운 구간이 추가된다
             */
            @DisplayName("지하철 노선의 구간 사이에 구간을 등록")
            @Test
            void addLineSectionInMiddle() {
                // given
                Long 정자역 = 신규_지하철역("정자역");
                지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역,6L));

                Long 판교역 = 신규_지하철역("판교역");

                // when
                지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(판교역, 정자역, 3L));

                // then
                ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 판교역, 정자역);
            }
        }

        @Nested
        @DisplayName("실패")
        class fail {
            /**
             * given 새로운 구간이 기존 역 사이 길이보다 크고,
             * When 지하철 노선의 기존의 구간에 구간 추가를 요청 하면
             * Then 구간 사이에 새로운 구간이 추가에 실패한다.
             */
            @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거면 등록 실패")
            @Test
            void addLineSectionInMiddleFail() {
                // given
                Long 정자역 = 신규_지하철역("정자역");
                지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역,6L));

                Long 판교역 = 신규_지하철역("판교역");

                // when
                ExtractableResponse<Response> addResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(판교역, 정자역, 7L));

                // then
                ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
                assertAll(
                        () -> assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                        () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역)
                );
            }

            /**
             * given 새로운 구간이 기존 역 사이 길이보다 크거나 같고,
             * When 지하철 노선의 기존의 구간에 구간 추가를 요청 하면
             * Then 구간 사이에 새로운 구간이 추가에 실패한다.
             */
            @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 같으면 등록 실패")
            @Test
            void addLineSectionInMiddleFail2() {
                // given
                Long 정자역 = 신규_지하철역("정자역");
                지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역,6L));

                Long 판교역 = 신규_지하철역("판교역");

                // when
                ExtractableResponse<Response> addResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(판교역, 정자역, 6L));

                // then
                ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
                assertAll(
                        () -> assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                        () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역)
                );
            }

            /**
             * given 새로운 구간의 상행역과 하행역이 이미 노선에 모두 등록되어 있고,
             * When 지하철 노선의 기존의 구간에 구간 추가를 요청 하면
             * Then 구간 사이에 새로운 구간이 추가에 실패한다.
             */
            @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록 실패")
            @Test
            void addLineSectionDuplicationFail() {
                // given
                Long 정자역 = 신규_지하철역("정자역");
                지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역,6L));

                // when
                ExtractableResponse<Response> addResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역, 2L));

               // then
                ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
                assertAll(
                        () -> assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                        () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역)
                );
            }

            /**
             * given 상행역과 하행역 둘 중 하나도 포함되어있지 않으면,
             * When 지하철 노선의 기존의 구간에 구간 추가를 요청 하면
             * Then 구간 사이에 새로운 구간이 추가에 실패한다.
             */
            @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록 실패")
            @Test
            void addLineSectionNotFountStation() {
                // given
                Long 정자역 = 신규_지하철역("정자역");
                지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역,6L));

                Long 신도림역 = 신규_지하철역("신도림역");
                Long 구로역 = 신규_지하철역("구로역");

                // when
                ExtractableResponse<Response> addResponse = 지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(신도림역, 구로역, 2L));

                // then
                ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
                assertAll(
                        () -> assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                        () -> assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 양재역, 정자역)
                );
            }
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
        Long 정자역 = 신규_지하철역("정자역");
        지하철_노선에_지하철_구간_생성_요청(신분당선, 신규_구간(양재역, 정자역, 6L));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getStationsId(response)).containsExactly(강남역, 양재역);
    }

    private List<Long> getStationsId(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    private Map<String, String> 신규_라인(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> 신규_구간(Long upStationId, Long downStationId, Long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
