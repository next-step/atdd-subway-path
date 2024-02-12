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

import static nextstep.config.fixtures.LineFixture.호남선;
import static nextstep.config.fixtures.SectionFixture.지하철_구간;
import static nextstep.config.fixtures.StationFixture.역_10개;
import static nextstep.subway.steps.StationLineSteps.지하철_노선_생성_요청_검증_포함;
import static nextstep.subway.steps.StationLineSteps.지하철_노선_조회_요청;
import static nextstep.subway.steps.StationSectionSteps.*;
import static nextstep.subway.steps.StationSteps.지하철_역_생성_요청;
import static nextstep.subway.utils.HttpResponseUtils.getCreatedLocationId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("지하철 구간 관리")
@AcceptanceTest
public class StationSectionAcceptanceTest {

    @BeforeEach
    void 초기_지하철_역_설정() {
        지하철_역_생성_요청(역_10개);
    }

    @Nested
    class 공통_실패 {
        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 생성할 때, 상행역이 존재하지 않는 역일 경우
         * Then  지하철 구간 생성에 실패한다.
         */
        @ParameterizedTest
        @ValueSource(longs = {-100L, -1L, 0L})
        void 상행역이_존재하지_않는_역일_경우(Long upStationId) {
            // given
            ExtractableResponse<Response> response =
                    지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));

            // when
            ExtractableResponse<Response> 실패하는_생성요청_응답 =
                    지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(upStationId, 2L, 10));

            // then
            assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 생성할 때, 하행역 존재하지 않는 역일 경우
         * Then  지하철 구간 생성에 실패한다.
         */
        @ParameterizedTest
        @ValueSource(longs = {-100L, -1L, 0L})
        void 하행역이_존재하지_않는_역일_경우(Long downStationId) {
            // given
            ExtractableResponse<Response> response =
                    지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));

            // when
            ExtractableResponse<Response> 실패하는_생성요청_응답 =
                    지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(2L, downStationId, 10));

            // then
            assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        }

        /**
         * Given 지하철 노선이 생성되고
         * When  지하철 구간을 생성할 때, 거리를 1보다 작은 숫자로 요청하면
         * Then  지하철 구간 생성에 실패한다.
         */
        @ParameterizedTest
        @ValueSource(ints = {-100, -1, 0})
        void 거리가_1보다_작은_숫자일_경우(int distance) {
            // given
            ExtractableResponse<Response> response =
                    지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));

            // when
            ExtractableResponse<Response> 실패하는_생성요청_응답 =
                    지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(2L, 3L, distance));

            // then
            assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class 구간_생성 {

        @Nested
        class 성공 {

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 생성할 때,
             * When  요청한 상행역이 하행 종점역으로 등록되어 있으면서
             * When  요청한 하행역이 구간으로 등록되어 있지 않을 경우
             * Then  지하철 구간 등록에 성공한다.
             */
            @Test
            void 상행역이_하행_종점역으로_등록되어_있고_하행역이_구간으로_등록되지_않은_역일_경우_등록_성공() {
                // given
                ExtractableResponse<Response> response =
                        지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));

                // when
                ExtractableResponse<Response> 성공하는_생성요청_응답 =
                        지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(2L, 4L, 10));

                // then
                assertThat(성공하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            }
        }

        @Nested
        class 상행역으로_인한_실패 {

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 생성할 때,
             * When  요청한 상행역이 하행 종점역으로 등록되어 있지 않을 경우
             * Then  지하철 구간 등록에 실패한다.
             */
            @Test
            void 상행역이_하행_종점역으로_등록되어_있지_않을_경우_등록_실패() {
                // given
                ExtractableResponse<Response> response =
                        지하철_노선_생성_요청_검증_포함(호남선(1L, 4L));

                ExtractableResponse<Response> 실패하는_생성요청_응답 =
                        지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(5L, 10L, 10));

                // then
                assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 생성할 때,
             * When  요청한 상행역이 역으로 등록되어 있지 않을 경우
             * Then  지하철 구간 등록에 실패한다.
             */
            @Test
            void 상행역이_역_등록되어_있지_않은_경우_등록_실패() {
                // given
                ExtractableResponse<Response> response =
                        지하철_노선_생성_요청_검증_포함(호남선(1L, 10L));

                // when, 최초 10개의 역 생성(@BeforeEach)
                ExtractableResponse<Response> 실패하는_생성요청_응답 =
                        지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(11L, 3L, 10));

                // then
                assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 생성할 때,
             * When  요청한 상행역이 이미 구간으로 등록되어 있는 경우
             * Then  지하철 구간 등록에 실패한다.
             */
            @Test
            void 상행역이_이미_구간에_등록되어_있는_경우_등록_실패() {
                // given
                ExtractableResponse<Response> response =
                        지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));
                지하철_구간_생성요청_상태코드_검증_포함(getCreatedLocationId(response), 지하철_구간(2L, 3L, 10));

                // when
                ExtractableResponse<Response> 실패하는_생성요청_응답 =
                        지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(2L, 4L, 10));

                // then
                assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }
        }

        @Nested
        class 하행역으로_인한_실패 {

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 생성할 때,
             * When  요청한 하행역이 이미 하행 종점역으로 등록되어 있는 경우
             * Then  지하철 구간 등록에 실패한다.
             */
            @Test
            void 하행역이_이미_하행_종점역으로_등록되어_있는_경우_등록_실패() {
                // given
                ExtractableResponse<Response> response =
                        지하철_노선_생성_요청_검증_포함(호남선(1L, 4L));

                // when
                ExtractableResponse<Response> 실패하는_생성요청_응답 =
                        지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(4L, 4L, 10));

                // then
                assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 생성할 때,
             * When  요청한 하행역이 역으로 등록되어 있지 않은 경우
             * Then  지하철 구간 등록에 실패한다.
             */
            @Test
            void 하행역이_역으로_등록되어_있지_않은_경우_등록_실패() {
                // given
                ExtractableResponse<Response> response =
                        지하철_노선_생성_요청_검증_포함(호남선(1L, 10L));

                // when, 최초 10개의 역 생성(@BeforeEach)
                ExtractableResponse<Response> 실패하는_생성요청_응답 =
                        지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(10L, 11L, 10));

                // then
                assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            }

            /**
             * Given 지하철 노선이 생성되고
             * When  지하철 구간을 생성할 때,
             * When  요청한 하행역이 이미 구간으로 등록되어 있는 경우
             * Then  지하철 구간 등록에 실패한다.
             */
            @Test
            void 하행역이_이미_구간에_등록되어_있는_경우_등록_실패() {
                // given
                ExtractableResponse<Response> response =
                        지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));
                지하철_구간_생성요청_상태코드_검증_포함(getCreatedLocationId(response), 지하철_구간(2L, 3L, 10));

                // when
                ExtractableResponse<Response> 실패하는_생성요청_응답 =
                        지하철_구간_생성요청_검증_생략(getCreatedLocationId(response), 지하철_구간(3L, 2L, 10));

                // then
                assertThat(실패하는_생성요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
                    지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));
            SectionRequest 생성할_지하철_구간 = 지하철_구간(2L, 4L, 10);
            지하철_구간_생성요청_상태코드_검증_포함(getCreatedLocationId(response), 생성할_지하철_구간);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(생성할_지하철_구간.getDownStationId(), getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(1L, 2L));

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
            LineRequest 호남선 = 호남선(1L, 2L);
            ExtractableResponse<Response> response = 지하철_노선_생성_요청_검증_포함(호남선);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(호남선.getDownStationId(), getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(1L, 2L));
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
                    지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));
            SectionRequest 생성할_지하철_구간 = 지하철_구간(2L, 4L, 10);
            지하철_구간_생성요청_상태코드_검증_포함(getCreatedLocationId(response), 생성할_지하철_구간);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(생성할_지하철_구간.getUpStationId(), getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(1L, 2L, 4L));
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
                    지하철_노선_생성_요청_검증_포함(호남선(1L, 2L));

            SectionRequest 생성할_지하철_구간 = 지하철_구간(2L, 4L, 10);
            지하철_구간_생성요청_상태코드_검증_포함(getCreatedLocationId(response), 생성할_지하철_구간);

            // then
            ExtractableResponse<Response> 성공하는_삭제요청_응답 =
                    지하철_구간_삭제요청_검증_생략(100L, getCreatedLocationId(response));

            // then
            assertThat(성공하는_삭제요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(convertStationIds(지하철_노선_조회_요청(getCreatedLocationId(response))))
                    .isEqualTo(List.of(1L, 2L, 4L));
        }

        private List<Long> convertStationIds(JsonPath jsonPath) {
            return jsonPath.getList("stations.id", Long.class);
        }
    }
}