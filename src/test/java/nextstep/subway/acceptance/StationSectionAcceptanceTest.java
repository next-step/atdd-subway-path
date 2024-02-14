package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.config.annotations.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static nextstep.config.fixtures.LineFixture.호남선;
import static nextstep.config.fixtures.SectionFixture.지하철_구간;
import static nextstep.config.fixtures.StationFixture.역_10개;
import static nextstep.subway.steps.StationLineSteps.지하철_노선_생성_요청_검증_포함;
import static nextstep.subway.steps.StationLineSteps.지하철_노선_조회_요청;
import static nextstep.subway.steps.StationSectionSteps.*;
import static nextstep.subway.steps.StationSteps.*;
import static nextstep.subway.utils.HttpResponseUtils.getCreatedLocationId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 구간 관리")
@AcceptanceTest
public class StationSectionAcceptanceTest {

    Long 가산디지털단지역_번호;
    Long 구로디지털단지역_번호;
    Long 독산역_번호;
    Long 신도림역_번호;
    Long 홍대입구역_번호;
    Long 종각역_번호;
    Long 신림역_번호;
    Long 잠실역_번호;
    Long 교대역_번호;
    Long 서울역_번호;


    @BeforeEach
    void 초기_지하철_역_설정() {
        지하철_역_생성_요청(역_10개);

        Map<String, Long> 지하철역_맵 = convertStationResponses(지하철_역_목록_조회().jsonPath());
        가산디지털단지역_번호 = 지하철역_맵.get("가산디지털단지");
        구로디지털단지역_번호 = 지하철역_맵.get("구로디지털단지");
        독산역_번호 = 지하철역_맵.get("독산역");
        신도림역_번호 = 지하철역_맵.get("신도림");
        홍대입구역_번호 = 지하철역_맵.get("홍대입구");
        종각역_번호 = 지하철역_맵.get("종각");
        신림역_번호 = 지하철역_맵.get("신림");
        잠실역_번호 = 지하철역_맵.get("잠실");
        교대역_번호 = 지하철역_맵.get("교대");
        서울역_번호 = 지하철역_맵.get("서울역");
    }

    @Nested
    class 구간_등록 {
        ExtractableResponse<Response> 호남선_생성_응답;

        @BeforeEach
        void 초기_지하철_구간_설정() {
            호남선_생성_응답 = 지하철_노선_생성_요청_검증_포함(
                    호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10));

            지하철_구간_목록_추가요청_상태코드_검증_포함(getCreatedLocationId(호남선_생성_응답),
                    List.of(
                            지하철_구간(구로디지털단지역_번호, 신도림역_번호, 10),
                            지하철_구간(신도림역_번호, 서울역_번호, 10),
                            지하철_구간(서울역_번호, 홍대입구역_번호, 10)));
        }

        @Nested
        class 성공 {

            @Nested
            class 노선_처음에_추가 {

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 처음에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있지 않으면서
                 * When  요청한 구간의 하행역이 기존 노선의 가장 앞쪽에 추가되어 있는 경우
                 * Then  지하철 구간 등록에 성공한다.
                 */
                @Test
                void 추가_요청한_구간의_상행역이_등록되어_있지_않으면서_하행역은_등록되지_등록된_역일_경우() {
                    // when
                    ExtractableResponse<Response> 성공하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(독산역_번호, 가산디지털단지역_번호, 4));

                    // then
                    assertThat(성공하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 처음에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 가장 앞쪽에 등록되어 있으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있지 않을 경우
                 * Then  지하철 구간 등록에 성공한다.
                 */
                @Test
                void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있고_하행역은_등록되지_않은_역일_경우() {
                    // when
                    ExtractableResponse<Response> 성공하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(구로디지털단지역_번호, 종각역_번호, 4));

                    // then
                    assertThat(성공하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }
            }

            @Nested
            class 노선_중간에_추가 {

                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 노선 중간에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있지 않으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있는 경우
                 * Then  지하철 구간 등록에 성공한다.
                 */
                @Test
                void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되지_않은_역이면서_하행역은_등록된_역일_경우() {
                    // when
                    ExtractableResponse<Response> 성공하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(종각역_번호, 서울역_번호, 4));

                    // then
                    assertThat(성공하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }

                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 노선 중간에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있지 않을 경우
                 * Then  지하철 구간 등록에 성공한다.
                 */
                @Test
                void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있고_하행역은_등록되지_않은_역일_경우() {
                    // when
                    ExtractableResponse<Response> 성공하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(신도림역_번호, 종각역_번호, 4));

                    // then
                    assertThat(성공하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }
            }

            @Nested
            class 노선_끝에_추가 {

                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 노선 끝에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있지 않으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있는 경우
                 * Then  지하철 구간 등록에 성공한다.
                 */
                @Test
                void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되지_않은_역이면서_하행역은_등록된_역일_경우() {
                    // when
                    ExtractableResponse<Response> 성공하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(종각역_번호, 홍대입구역_번호, 4));

                    // then
                    assertThat(성공하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }

                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 노선 끝에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 등록되어 있지 않을 경우
                 * Then  지하철 구간 등록에 성공한다.
                 */
                @Test
                void 추가_요청한_구간의_상행역이_기존_노선의_역으로_등록되어_있고_하행역은_등록되지_않은_역일_경우() {
                    // when
                    ExtractableResponse<Response> 성공하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(홍대입구역_번호, 종각역_번호, 4));

                    // then
                    assertThat(성공하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                }
            }
        }

        @Nested
        class 실패 {

            @Nested
            class 공통 {

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있지 않은 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 추가_요청한_구간의_상행역이_기존_노선에_등록되지_않았을_경우() {
                    // when, 최초 10개의 역 생성(@BeforeEach)
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(20L, 종각역_번호, 3));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 추가할 때,
                 * When  요청한 구간의 상행역이 존재하지 않는 역일 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @ParameterizedTest
                @ValueSource(longs = {-100L, -1, 0L})
                void 추가_요청한_상행역이_존재하지_않는_역일_경우(Long upStationId) {
                    // given
                    ExtractableResponse<Response> response =
                            지하철_노선_생성_요청_검증_포함(호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10));

                    // when
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(response), 지하철_구간(upStationId, 구로디지털단지역_번호, 10));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 추가할 때,
                 * When  요청한 구간의 하행역 존재하지 않는 역일 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @ParameterizedTest
                @ValueSource(longs = {-100L, -1, 0L})
                void 추가_요청한_하행역이_존재하지_않는_역일_경우(Long downStationId) {
                    // given
                    ExtractableResponse<Response> response =
                            지하철_노선_생성_요청_검증_포함(호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10));

                    // when
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(response), 지하철_구간(구로디지털단지역_번호, downStationId, 10));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

                }

                /**
                 * Given 지하철 노선이 생성되고
                 * When  지하철 구간을 추가할 때, 거리를 1보다 작은 숫자로 요청하면
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @ParameterizedTest
                @ValueSource(ints = {-100, -1, 0})
                void 거리가_1보다_작은_숫자일_경우(int distance) {
                    // given
                    ExtractableResponse<Response> response =
                            지하철_노선_생성_요청_검증_포함(호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10));

                    // when
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(response), 지하철_구간(구로디지털단지역_번호, 독산역_번호, distance));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

            }

            @Nested
            class 노선_처음에_추가 {

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 처음에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 동록되어 있는 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 상행역이_등록되어_있으면서_하행역이_노선의_역으로_등록되어_있는_경우() {
                    // when
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(구로디지털단지역_번호, 서울역_번호, 3));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 처음에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 역으로 등록되어 있지 않을 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 상행역이_등록되어_있으면서_하행역이_역으로_등록되어_있지_않은_경우() {
                    // when, 최초 10개의 역 생성(@BeforeEach)
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(구로디지털단지역_번호, 20L, 3));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 처음에 추가할 때
                 * When  요청한 구간의 하행역을 기준으로 직후 역과의 거리가 1보다 작은 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 요청한_구간의_하행역을_기준으로_직후_역과의_거리가_1보다_작은_경우() {
                    // when, 최초 10개의 역 생성(@BeforeEach)
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(구로디지털단지역_번호, 20L, 10));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }
            }

            @Nested
            class 노선_중간에_추가 {

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 중간에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 동록되어 있는 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 상행역이_등록되어_있으면서_하행역이_노선의_역으로_등록되어_있는_경우() {
                    // when
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(신도림역_번호, 홍대입구역_번호, 3));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 중간에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 역으로 등록되어 있지 않을 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 상행역이_등록되어_있으면서_하행역이_역으로_등록되어_있지_않은_경우() {
                    // when, 최초 10개의 역 생성(@BeforeEach)
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(신도림역_번호, 20L, 3));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 중간에 추가할 때
                 * When  요청한 구간의 하행역을 기준으로 직후 역과의 거리가 1보다 작은 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 요청한_구간의_하행역을_기준으로_직후_역과의_거리가_1보다_작은_경우() {
                    // when
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(신도림역_번호, 종각역_번호, 10));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

            }

            @Nested
            class 노선_끝에_추가 {

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 처음에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 기존 노선의 역으로 동록되어 있는 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 상행역이_등록되어_있으면서_하행역이_노선의_역으로_등록되어_있는_경우() {
                    // when
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(홍대입구역_번호, 구로디지털단지역_번호, 3));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }

                /**
                 * Given 지하철 노선이 생성되고, 구간을 추가한다.
                 * When  지하철 구간을 노선 처음에 추가할 때
                 * When  요청한 구간의 상행역이 기존 노선의 역으로 등록되어 있으면서
                 * When  요청한 구간의 하행역이 역으로 등록되어 있지 않을 경우
                 * Then  지하철 구간 등록에 실패한다.
                 */
                @Test
                void 상행역이_등록되어_있으면서_하행역이_역으로_등록되어_있지_않은_경우() {
                    // when, 최초 10개의 역 생성(@BeforeEach)
                    ExtractableResponse<Response> 실패하는_생성요청_응답 =
                            지하철_구간_추가요청_검증_생략(getCreatedLocationId(호남선_생성_응답), 지하철_구간(홍대입구역_번호, 20L, 3));

                    // then
                    assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                }
            }

        }

    }

    @Nested
    class 구간_삭제 {

        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 제거할 때,
         * When  구간이 최소 1개 이상 존재하고, 하행 종점역이 포함된 구간을 삭제하는 경우
         * Then  지하철 구간 삭제에 성공한다.
         */
        @Test
        void 구간이_한개_이상_존재하고_하행_종점역이_포함된_구간을_삭제하는_경우_삭제_성공() {
            // given
            ExtractableResponse<Response> response =
                    지하철_노선_생성_요청_검증_포함(호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10));
            SectionRequest 생성할_지하철_구간 = 지하철_구간(구로디지털단지역_번호, 신도림역_번호, 10);
            지하철_구간_추가요청_상태코드_검증_포함(getCreatedLocationId(response), 생성할_지하철_구간);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(생성할_지하철_구간.getDownStationId(), getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(가산디지털단지역_번호, 구로디지털단지역_번호));

        }

        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 제거할 때,
         * When  기존 1개의 구간만 존재할 경우(역이 2개만 존재할 경우)
         * Then  지하철 구간 삭제에 실패한다.
         */
        @Test
        void 한개의_구간만_존재할_경우_삭제_실패() {
            // given
            LineRequest 호남선 = 호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10);
            ExtractableResponse<Response> response = 지하철_노선_생성_요청_검증_포함(호남선);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(호남선.getDownStationId(), getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(가산디지털단지역_번호, 구로디지털단지역_번호));
        }

        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 제거할 때,
         * When  하행 종점역을 제거하는 것이 아닌 경우
         * Then  지하철 구간 삭제에 실패한다.
         */
        @Test
        void 하행_종점역을_제거하는_것이_아닌_경우_삭제_실패() {
            // given
            ExtractableResponse<Response> response =
                    지하철_노선_생성_요청_검증_포함(호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10));
            SectionRequest 생성할_지하철_구간 = 지하철_구간(구로디지털단지역_번호, 신도림역_번호, 10);
            지하철_구간_추가요청_상태코드_검증_포함(getCreatedLocationId(response), 생성할_지하철_구간);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(생성할_지하철_구간.getUpStationId(), getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(가산디지털단지역_번호, 구로디지털단지역_번호, 신도림역_번호));
        }

        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 제거할 때,
         * When  존재하지 않는 역을 제거하는 경우
         * Then  지하철 구간 삭제에 실패한다.
         */
        @Test
        void 존재하지_않는_역을_제거하는_경우_삭제_실패() {
            // given
            ExtractableResponse<Response> response =
                    지하철_노선_생성_요청_검증_포함(호남선(가산디지털단지역_번호, 구로디지털단지역_번호, 10));

            SectionRequest 생성할_지하철_구간 = 지하철_구간(구로디지털단지역_번호, 신도림역_번호, 10);
            지하철_구간_추가요청_상태코드_검증_포함(getCreatedLocationId(response), 생성할_지하철_구간);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(100L, getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(가산디지털단지역_번호, 구로디지털단지역_번호, 신도림역_번호));
        }

        private List<Long> convertStationIds(JsonPath jsonPath) {
            return jsonPath.getList("stations.id", Long.class);
        }
    }
}