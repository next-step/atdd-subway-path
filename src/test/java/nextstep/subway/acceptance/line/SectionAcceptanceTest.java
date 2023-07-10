package nextstep.subway.acceptance.line;

import static org.assertj.core.api.Assertions.assertThat;

import static nextstep.subway.acceptance.line.LineSteps.지하철노선을_생성한다;
import static nextstep.subway.acceptance.line.LineSteps.지하철노선을_조회한다;
import static nextstep.subway.acceptance.line.SectionSteps.지하철구간_등록에_실패한다;
import static nextstep.subway.acceptance.line.SectionSteps.지하철구간_제거에_실패한다;
import static nextstep.subway.acceptance.line.SectionSteps.지하철구간을_등록한다;
import static nextstep.subway.acceptance.line.SectionSteps.지하철구간을_제거한다;
import static nextstep.subway.acceptance.station.StationSteps.지하철역을_생성한다;
import static nextstep.subway.unit.LineFixture.DEFAULT_LINE_LENGTH;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.applicaion.line.request.LineCreateRequest;
import nextstep.subway.applicaion.line.request.SectionRequest;
import nextstep.subway.applicaion.station.response.StationResponse;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long 교대역, 강남역, 역삼역, 선릉역, 삼성역;

    @BeforeEach
    public void setUp() {
        교대역 = 지하철역을_생성한다("교대역").getId();
        강남역 = 지하철역을_생성한다("강남역").getId();
        역삼역 = 지하철역을_생성한다("역삼역").getId();
        선릉역 = 지하철역을_생성한다("선릉역").getId();
        삼성역 = 지하철역을_생성한다("삼성역").getId();
    }

    @DisplayName("지하철 구간 등록")
    @Nested
    class AddSectionTest {

        @DisplayName("지하철 구간 등록에 성공한다")
        @Nested
        class Success {

            /**
             * Given 지하철 노선을 생성하고
             * When 하행역이 노선의 상행 종점역과 동일한 지하철 구간을 등록하면
             * Then 등록된 구간을 응답받을 수 있다.
             */
            @Test
            void 노선_앞에_구간을_추가한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 10);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when
                final var response = 지하철구간을_등록한다(lineId, new SectionRequest(교대역, 강남역, 10));

                // then
                final var stationIds = response.getStations().stream().map(StationResponse::getId);
                assertThat(stationIds).containsExactly(교대역, 강남역, 역삼역);
            }

            /**
             * Given 지하철 노선을 생성하고
             * When 상행역이 노선의 하행 종점역과 동일한 지하철 구간을 등록하면
             * Then 등록된 구간을 응답받을 수 있다.
             */
            @Test
            void 노선_뒤에_구간을_추가한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 10);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when
                final var response = 지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                // then
                final var stationIds = response.getStations().stream().map(StationResponse::getId);
                assertThat(stationIds).containsExactly(강남역, 역삼역, 선릉역);

            }

            @Nested
            class 노선_중간에_구간을_추가한다 {

                /**
                 * Given 지하철 노선을 생성하고
                 * When 상행역이 동일한 지하철 구간을 등록하면
                 * Then 등록된 구간을 응답받을 수 있다.
                 */
                @Test
                void 상행역이_동일한_구간을_추가한다() {
                    final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                    // given
                    final var lineId = 지하철노선을_생성한다(request).getId();

                    // when
                    final var response = 지하철구간을_등록한다(lineId, new SectionRequest(강남역, 역삼역, 10));

                    // then
                    final var stationIds = response.getStations().stream().map(StationResponse::getId);
                    assertThat(stationIds).containsExactly(강남역, 역삼역, 선릉역);
                }

                /**
                 * Given 지하철 노선을 생성하고
                 * When 상행역이 동일한 지하철 구간을 등록하면
                 * Then 등록된 구간을 응답받을 수 있다.
                 */
                @Test
                void 하행역이_동일한_구간을_추가한다() {
                    final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                    // given
                    final var lineId = 지하철노선을_생성한다(request).getId();

                    // when
                    final var response = 지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                    // then
                    final var stationIds = response.getStations().stream().map(StationResponse::getId);
                    assertThat(stationIds).containsExactly(강남역, 역삼역, 선릉역);
                }
            }
        }

        @DisplayName("지하철 구간 등록에 실패한다")
        @Nested
        class Fail {

            /**
             * Given 지하철 노선을 생성하고
             * When 지하철 구간을 등록할때
             * When 새로운 구간의 상행역과 하행역이 모두 노선에 등록되어 있다면
             * Then 새로운 구간을 등록할 수 없다
             */
            @Test
            void 상행역과_하행역_모두_노선에_포함되어_있으면_안된다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_등록에_실패한다(lineId, new SectionRequest(강남역, 선릉역, 10));
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

            /**
             * Given 지하철 노선을 생성하고
             * When 지하철 구간을 등록할때
             * When 새로운 구간의 상행역과 하행역이 모두 노선에 등록되어 있지 않다면
             * Then 새로운 구간을 등록할 수 없다
             */
            @Test
            void 상행역과_하행역_둘중_하나도_노선에_포함되어_있지_않으면_안된다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_등록에_실패한다(lineId, new SectionRequest(교대역, 삼성역, 10));
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

            /**
             * Given 지하철 노선을 생성하고
             * When 지하철 구간을 등록할때
             * When 새로운 구간이 기존의 역 사이에 등록되는 경우
             * When 새로운 구간의 길이가 기존 역 사이의 길이보다 크거나 같으면
             * Then 새로운 구간을 등록할 수 없다
             */
            @ParameterizedTest
            @ValueSource(ints = {DEFAULT_LINE_LENGTH, DEFAULT_LINE_LENGTH + 1})
            void 역_사이에_새로운_역을_등록할_경우_새로운_구간의_길이는_기존_역_사이_길이보다_크거나_같아선_안된다(final int length) {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, DEFAULT_LINE_LENGTH);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_등록에_실패한다(lineId, new SectionRequest(강남역, 역삼역, length));
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }
        }
    }

    @DisplayName("지하철 구간 삭제")
    @Nested
    class RemoveSectionTest {

        /**
         * Given 지하철 노선을 생성하고
         * Given 지하철 구간을 등록하고
         * When 등록한 지하철 구간을 삭제하면
         * Then 해당 지하철 구간 정보는 삭제된다
         */
        @DisplayName("지하철 구간 제거에 성공한다")
        @Test
        void success() {
            final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

            // given
            final var lineId = 지하철노선을_생성한다(request).getId();
            지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

            // when
            지하철구간을_제거한다(lineId, 선릉역);

            // then
            assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
        }

        @DisplayName("지하철 구간 제거에 실패한다")
        @Nested
        class Fail {

            /**
             * Given 지하철 노선을 생성하고
             * Given 지하철 구간을 등록하고
             * When 등록한 지하철 구간을 삭제할때
             * When 삭제하고자 하는 구간이 마지막 구간이 아니라면
             * Then 해당 지하철 구간 정보는 삭제할 수 없다
             */
            @Test
            void 삭제하고자_하는_구간이_마지막_구간이_아닌_경우() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();
                지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                // when & then
                지하철구간_제거에_실패한다(lineId, 역삼역);
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(3);
            }

            /**
             * Given 지하철 노선을 생성하고
             * Given 지하철 구간을 등록하고
             * When 등록한 지하철 구간을 삭제할때
             * When 노선에 상행 종점역과 하행 종점역만 있는 경우
             * Then 해당 지하철 구간 정보는 삭제할 수 없다
             */
            @Test
            void 노선에_상행_종점역과_하행_종점역만_있는_경우() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();
                지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                // when & then
                지하철구간을_제거한다(lineId, 선릉역);
                지하철구간_제거에_실패한다(lineId, 역삼역);
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }
        }
    }
}
