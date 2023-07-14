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

        @Nested
        class Success {

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

        @Nested
        class Fail {

            @Test
            void 상행역과_하행역_모두_노선에_포함되어_있으면_안된다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_등록에_실패한다(lineId, new SectionRequest(강남역, 선릉역, 10));
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

            @Test
            void 상행역과_하행역_둘중_하나도_노선에_포함되어_있지_않으면_안된다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_등록에_실패한다(lineId, new SectionRequest(교대역, 삼성역, 10));
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

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

            @Test
            void 구간을_등록하고자_하는_노선은_등록되어_있어야_한다() {
                지하철구간_등록에_실패한다(0L, new SectionRequest(교대역, 삼성역, 10));
            }

            @Test
            void 등록하고자_하는_구간의_상행역은_등록되어_있어야_한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_등록에_실패한다(lineId, new SectionRequest(0L, 삼성역, 10));
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

            @Test
            void 등록하고자_하는_구간의_하행역은_등록되어_있어야_한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_등록에_실패한다(lineId, new SectionRequest(교대역, 0L, 10));
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }
        }
    }

    @DisplayName("지하철 구간 삭제")
    @Nested
    class RemoveSectionTest {

        @Nested
        class success {

            @Test
            void 지하철_구간의_상행_종점역을_삭제한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();
                지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                // when
                지하철구간을_제거한다(lineId, 강남역);

                // then
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

            @Test
            void 지하철_구간의_중간_역을_삭제한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();
                지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                // when
                지하철구간을_제거한다(lineId, 역삼역);

                // then
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

            @Test
            void 지하철_구간의_하행_종점역을_삭제한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();
                지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                // when
                지하철구간을_제거한다(lineId, 선릉역);

                // then
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }
        }

        @Nested
        class Fail {

            @Test
            void 노선에_등록되어_있지_않은_역이어야_한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();
                지하철구간을_등록한다(lineId, new SectionRequest(역삼역, 선릉역, 10));

                // when & then
                지하철구간_제거에_실패한다(lineId, 삼성역);
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(3);
            }

            @Test
            void 노선_내_구간이_하나뿐이라면_삭제할_수_없어야_한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 역삼역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_제거에_실패한다(lineId, 역삼역);
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }

            @Test
            void 역을_삭제하고자_하는_노선은_등록되어_있어야_한다() {
                지하철구간_제거에_실패한다(0L, 교대역);
            }

            @Test
            void 삭제하고자_하는_역은_등록되어_있어야_한다() {
                final var request = new LineCreateRequest("2호선", "bg-red-600", 강남역, 선릉역, 20);

                // given
                final var lineId = 지하철노선을_생성한다(request).getId();

                // when & then
                지하철구간_제거에_실패한다(lineId, 0L);
                assertThat(지하철노선을_조회한다(lineId).getStations()).hasSize(2);
            }
        }
    }
}
