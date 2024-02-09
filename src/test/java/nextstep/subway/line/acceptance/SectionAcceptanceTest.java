package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.core.AcceptanceTest;
import nextstep.core.RestAssuredHelper;
import nextstep.subway.common.api.LineApiHelper;
import nextstep.subway.common.api.SectionApiHelper;
import nextstep.subway.common.api.StationApiHelper;
import nextstep.subway.line.service.dto.LineResponse;
import nextstep.subway.line.service.dto.SectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 기능")
@AcceptanceTest
public class SectionAcceptanceTest {

    private Long 지하철역_Id;
    private Long 새로운지하철역_Id;
    private Long 또다른지하철역_Id;
    private Long 신분당선_Id;
    private Long 없는지하철역_Id;
    private Long 존재하지않는지하철역_Id;
    private final String 신분당선 = "신분당선";
    private final String 신분당선_color = "bg-red-600";
    private final int 신분당선_distance = 10;
    private final int 구간_distance = 5;


    @BeforeEach
    void setUp() {
        // given
        지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("지하철역"));
        새로운지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("새로운지하철역"));
        또다른지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("또다른지하철역"));
        없는지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("없는지하철역"));
        존재하지않는지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("존재하지않는지하철역"));
        신분당선_Id = RestAssuredHelper.getIdFrom((LineApiHelper.createLine(신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id, 신분당선_distance)));
    }

    @Nested
    @DisplayName("구간 생성")
    class Creation {
        /**
         * When 노선 마지막에 지하철 구간을 생성하면
         * Then 지하철 노선 조회시 구간 정보와 함께 조회할 수 있다.
         */
        @DisplayName("마지막에 추가 성공")
        @Test
        void 구간_생성_마지막에_추가_성공_테스트() {
            // when
            final ExtractableResponse<Response> response = 구간_생성_요청(신분당선_Id, 새로운지하철역_Id, 또다른지하철역_Id, 구간_distance);

            // then
            지하철_노선_조회시_생성된_구간정보가_마지막에_포함되어있다(response);
        }

        /**
         * When 노선 가운데에 지하철 구간을 생성하면
         * Then 지하철 노선 조회시 가운데에 새로운 역이 추가된 구간 정보와 함께 조회된다.
         */
        @DisplayName("가운데에 추가 성공")
        @Test
        void 구간_생성_가운데에_추가_성공_테스트() {
            // when
            final ExtractableResponse<Response> response = 구간_생성_요청(신분당선_Id, 지하철역_Id, 또다른지하철역_Id, 구간_distance);

            // then
            지하철_노선_조회시_생성된_구간정보가_가운데에_포함되어있다(response);
        }

        /**
         * When 노선 가운데에 지하철 구간을 생성하면
         * Then 지하철 노선 조회시 처음에 새로운 역이 추가된 구간 정보와 함께 조회된다.
         */
        @DisplayName("처음에 추가 성공")
        @Test
        void 구간_생성_처음에_추가_성공_테스트() {
            // when
            final ExtractableResponse<Response> response = 구간_생성_요청(신분당선_Id, 또다른지하철역_Id, 지하철역_Id, 구간_distance);

            // then
            지하철_노선_조회시_생성된_구간정보가_처음에_포함되어있다(response);
        }

        /**
         * When 지하철 구간을 생성하는데
         * When 구간 상행역, 하행역 모두 해당 노선에 등록되어 있다면
         * Then 에러가 난다.
         */
        @DisplayName("실패 - 구간 상행역, 하행역 모두 해당 노선에 등록되어 있다면 실패한다.")
        @Test
        void 구간_생성_실패_구간_상행역_하행역_모두_해당_노선에_이미_등록되어_있을경우_테스트() {
            // when
            final ExtractableResponse<Response> response = 구간_생성_요청(신분당선_Id, 새로운지하철역_Id, 지하철역_Id, 구간_distance);

            // then
            지하철_구간이_변경되지_않는다(response);
        }

        /**
         * When 지하철 구간을 생성하는데
         * When 구간 상행역 하행역 모두 해당 노선에 포함되어 있지 않다면
         * Then 에러가 난다.
         */
        @DisplayName("실패 - 구간 상행역 하행역 모두 해당 노선에 포함되어 있지 않을 경우 실패한다.")
        @Test
        void 구간_생성_실패_구간_상행역_하행역_모두_해당에_포함되어_있지_않을_경우_테스트() {
            // when
            final ExtractableResponse<Response> response = 구간_생성_요청(신분당선_Id, 없는지하철역_Id, 존재하지않는지하철역_Id, 구간_distance);

            // then
            지하철_구간이_변경되지_않는다(response);
        }

        /**
         * When 지하철 구간을 가운데에 생성하는데
         * When 구간의 길이가 기존 노선의 길이보다 같거나 같다면
         * Then 에러가 난다.
         */
        @DisplayName("실패 - 가운데 구간의 길이가 지하철 노선의 길이보다 길다면 실패한다.")
        @Test
        void 구간_생성_실패_가운데_구간_길이가_기존_지하철_노선의_길이보다_길거나_같을_경우_테스트() {
            // when
            final ExtractableResponse<Response> response = 구간_생성_요청(신분당선_Id, 지하철역_Id, 또다른지하철역_Id, 신분당선_distance);

            // then
            지하철_구간이_변경되지_않는다(response);
        }

        private void 지하철_노선_조회시_생성된_구간정보가_마지막에_포함되어있다(final ExtractableResponse<Response> response) {
            assertAll(
                    () -> assertSoftly(softly -> {
                        softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                        final SectionResponse sectionResponse = response.as(SectionResponse.class);
                        softly.assertThat(sectionResponse.getUpStation().getId()).isEqualTo(새로운지하철역_Id);
                        softly.assertThat(sectionResponse.getDownStation().getId()).isEqualTo(또다른지하철역_Id);
                        softly.assertThat(sectionResponse.getDistance()).isEqualTo(구간_distance);
                    }),
                    SectionAcceptanceTest.this::assertSectionAddedAtLast
            );
        }

        private void 지하철_노선_조회시_생성된_구간정보가_가운데에_포함되어있다(final ExtractableResponse<Response> response) {
            assertAll(
                    () -> assertSoftly(softly -> {
                        softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                        final SectionResponse sectionResponse = response.as(SectionResponse.class);
                        softly.assertThat(sectionResponse.getUpStation().getId()).isEqualTo(지하철역_Id);
                        softly.assertThat(sectionResponse.getDownStation().getId()).isEqualTo(또다른지하철역_Id);
                        softly.assertThat(sectionResponse.getDistance()).isEqualTo(구간_distance);
                    }),
                    SectionAcceptanceTest.this::assertSectionAddedAtMiddle
            );
        }

        private void 지하철_노선_조회시_생성된_구간정보가_처음에_포함되어있다(final ExtractableResponse<Response> response) {
            assertAll(
                    () -> assertSoftly(softly -> {
                        softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                        final SectionResponse sectionResponse = response.as(SectionResponse.class);
                        softly.assertThat(sectionResponse.getUpStation().getId()).isEqualTo(또다른지하철역_Id);
                        softly.assertThat(sectionResponse.getDownStation().getId()).isEqualTo(지하철역_Id);
                        softly.assertThat(sectionResponse.getDistance()).isEqualTo(구간_distance);
                    }),
                    SectionAcceptanceTest.this::assertSectionAddedAtFirst
            );
        }

        private void 지하철_구간이_변경되지_않는다(final ExtractableResponse<Response> response) {
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                    SectionAcceptanceTest.this::assertSectionsNotChanged
            );
        }

    }


    @Nested
    @DisplayName("구간 제거")
    class Deletion {

        /**
         * Given 지하철 구간을 생성하고
         * When 마지막 지하철 구간을 제거하면
         * Then 지하철 노선 조회시 해당 구간 정보가 제외되고 조회된다.
         */
        @DisplayName("마지막 구간 제거 성공")
        @Test
        void 마지막_구간_제거_테스트() {
            // given
            구간_생성_요청(신분당선_Id, 새로운지하철역_Id, 또다른지하철역_Id, 구간_distance);

            // when
            final ExtractableResponse<Response> response = 구간_제거_요청(신분당선_Id, 또다른지하철역_Id);

            // then
            지하철_노선_조회시_마지막_구간_정보가_제외되어_조회된다(response);
        }
        
        /**
         * Given 지하철 구간을 생성하고
         * When 가운데 지하철 구간을 제거하면
         * Then 지하철 노선 조회시 해당 구간 정보가 제외되고 조회된다.
         */
        @DisplayName("가운데 구간 제거 성공")
        @Test
        void 가운데_구간_제거_테스트() {
            // given
            구간_생성_요청(신분당선_Id, 새로운지하철역_Id, 또다른지하철역_Id, 구간_distance);

            // when
            final ExtractableResponse<Response> response = 구간_제거_요청(신분당선_Id, 새로운지하철역_Id);

            // then
            지하철_노선_조회시_가운데_구간_정보가_제외되어_조회된다(response);
        }

        /**
         * When 지하철 구간을 제거하는데
         * When 해당 지하철 구간이 한개만 남아 있다면
         * Then 에러가 난다.
         */
        @DisplayName("실패 - 해당 지하철 노선에 구간이 한개만 남아 있다면 실패한다.")
        @Test
        void removeSectionFail_OnlyOneSectionLeftTest() {
            // when
            final ExtractableResponse<Response> response = 구간_제거_요청(신분당선_Id, 새로운지하철역_Id);

            // then
            지하철_구간이_변경되지않는다(response);
        }

        /**
         * Given 지하철 구간을 생성하고
         * When 지하철 구간을 제거하는데
         * When 해당 지하철 구간이 지하철 노선에 포함되어 있지 않는다면
         * Then 에러가 난다.
         */
        @DisplayName("실패 - 삭제 구간이 해당 지하철 노선에 존재하지 않는다면 실패한다.")
        @Test
        void createSectionFail_TargetSectionIsNotLastSectionTest() {
            // when
            final ExtractableResponse<Response> response = 구간_제거_요청(신분당선_Id, 또다른지하철역_Id);

            // then
            지하철_구간이_변경되지않는다(response);
        }

        private void 지하철_구간이_변경되지않는다(final ExtractableResponse<Response> response) {
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                    SectionAcceptanceTest.this::assertSectionsNotChanged
            );
        }

        private ExtractableResponse<Response> 구간_제거_요청(final Long 신분당선_id, final Long 또다른지하철역_id) {
            return SectionApiHelper.removeSection(신분당선_id, 또다른지하철역_id);
        }

        private void 지하철_노선_조회시_마지막_구간_정보가_제외되어_조회된다(final ExtractableResponse<Response> response) {
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                    SectionAcceptanceTest.this::assertSectionsNotChanged
            );
        }

        private void 지하철_노선_조회시_가운데_구간_정보가_제외되어_조회된다(final ExtractableResponse<Response> response) {
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                    SectionAcceptanceTest.this::assertSectionRemovedAtMiddle
            );
        }


    }

    private static ExtractableResponse<Response> 구간_생성_요청(final Long 신분당선_id, final Long 새로운지하철역_id, final Long 또다른지하철역_id, final int 구간_distance1) {
        return SectionApiHelper.createSection(신분당선_id, 새로운지하철역_id, 또다른지하철역_id, 구간_distance1);
    }

    private void assertSectionAddedAtLast() {
        assertSoftly(softly -> {
            final LineResponse lineResponse = LineApiHelper.fetchLineById(신분당선_Id).as(LineResponse.class);
            softly.assertThat(lineResponse.getDistance()).isEqualTo(신분당선_distance + 구간_distance);
            softly.assertThat(lineResponse.getStations())
                    .extracting("id").containsExactly(지하철역_Id, 새로운지하철역_Id, 또다른지하철역_Id);
        });
    }

    private void assertSectionAddedAtMiddle() {
        assertSoftly(softly -> {
            final LineResponse lineResponse = LineApiHelper.fetchLineById(신분당선_Id).as(LineResponse.class);
            softly.assertThat(lineResponse.getDistance()).isEqualTo(신분당선_distance);
            softly.assertThat(lineResponse.getStations())
                    .extracting("id").containsExactly(지하철역_Id, 또다른지하철역_Id, 새로운지하철역_Id);
        });
    }


    private void assertSectionAddedAtFirst() {
        assertSoftly(softly -> {
            final LineResponse lineResponse = LineApiHelper.fetchLineById(신분당선_Id).as(LineResponse.class);
            softly.assertThat(lineResponse.getDistance()).isEqualTo(신분당선_distance + 구간_distance);
            softly.assertThat(lineResponse.getStations())
                    .extracting("id").containsExactly(또다른지하철역_Id, 지하철역_Id, 새로운지하철역_Id);
        });
    }

    private void assertSectionsNotChanged() {
        assertSoftly(softly -> {
            final LineResponse lineResponse = LineApiHelper.fetchLineById(신분당선_Id).as(LineResponse.class);
            softly.assertThat(lineResponse.getDistance()).isEqualTo(신분당선_distance);
            softly.assertThat(lineResponse.getStations())
                    .extracting("id").containsExactly(지하철역_Id, 새로운지하철역_Id);
        });
    }

    private void assertSectionRemovedAtMiddle() {
        assertSoftly(softly -> {
            final LineResponse lineResponse = LineApiHelper.fetchLineById(신분당선_Id).as(LineResponse.class);
            softly.assertThat(lineResponse.getDistance()).isEqualTo(신분당선_distance + 구간_distance);
            softly.assertThat(lineResponse.getStations())
                    .extracting("id").containsExactly(지하철역_Id, 또다른지하철역_Id);
        });
    }


}
