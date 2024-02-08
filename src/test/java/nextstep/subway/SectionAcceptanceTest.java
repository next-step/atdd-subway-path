package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import nextstep.subway.controller.dto.LineCreateRequest;
import nextstep.subway.controller.dto.LineResponse;
import nextstep.subway.controller.dto.SectionCreateRequest;
import nextstep.subway.controller.dto.StationResponse;
import nextstep.subway.exception.ExceptionResponse;

import java.util.Map;
import java.util.stream.Stream;

import static nextstep.subway.fixture.LineFixture.SHINBUNDANG_LINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.http.HttpStatus.*;
import static nextstep.subway.fixture.StationFixture.*;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long 강남역_ID;
    private Long 선릉역_ID;
    private Long 양재역_ID;

    private Long 이호선;

    /**
     * GIVEN 지하철 역을 생성하고
     * GIVEN 노선을 생성한다
     */
    @BeforeEach
    void setFixture() {
        강남역_ID = 지하철역_생성_요청(GANGNAM_STATION.toCreateRequest(), CREATED.value())
                .as(StationResponse.class).getId();

        선릉역_ID = 지하철역_생성_요청(SEOLLEUNG_STATION.toCreateRequest(), CREATED.value())
                .as(StationResponse.class).getId();

        양재역_ID = 지하철역_생성_요청(YANGJAE_STATION.toCreateRequest(), CREATED.value())
                .as(StationResponse.class).getId();

        LineCreateRequest request = SHINBUNDANG_LINE.toCreateRequest(강남역_ID, 선릉역_ID);
        이호선 = 노선_생성_요청(request, CREATED.value())
                .as(LineResponse.class).getId();
    }

    /**
     * WHEN 새로운 지하철 구간 생성시 상행 지하철역과 하행 지하철역을 생성하지 않으면
     * Then 새로운 구간을 생성할 수 없다
     */
    @ParameterizedTest
    @MethodSource("provideBlankSectionCreateRequest")
    void 실패_새로운_지하철_구간_생성시_필수값을_모두_입력하지_않으면_예외가_발생한다(SectionCreateRequest request) {
        // when
        ExtractableResponse<Response> response = post("/lines/{lineId}/sections", request, BAD_REQUEST.value(), 이호선);

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    private static Stream<Arguments> provideBlankSectionCreateRequest() {
        return Stream.of(
                Arguments.of(
                        SectionCreateRequest
                                .builder()
                                .upStationId(1L)
                                .build()),
                Arguments.of(SectionCreateRequest
                        .builder()
                        .downStationId(2L)
                        .build())
        );
    }

    /**
     * WHEN 새로운 지하철 구간이 기존 구간과 같다면
     * Then 새로운 구간을 생성할 수 없다
     */
    @Test
    void 실패_새로운_구간_생성시_기존_구간과_같다면_구간을_생성할_수_없다() {
        // given
        SectionCreateRequest request = sectionCreateRequest(강남역_ID, 선릉역_ID, 10);

        // when
        String message = 구간_생성_요청(request, OK.value())
                .as(ExceptionResponse.class).getMessage();

        // then
        assertThat(message).isEqualTo("신규 구간이 기존 구간과 일치하여 구간을 생성할 수 없습니다.");
    }

    /**
     * WHEN 새로운 지하철 구간 생성시 노선의 처음에 생성하면
     * Then 새로운 구간이 생성된다
     */
    @Test
    void 성공_새로운_지하철_구간_생성시_노선의_처음에_생성할_수_있다() {
        // given
        SectionCreateRequest request = sectionCreateRequest(양재역_ID, 강남역_ID, 10);

        // when
        구간_생성_요청(request, CREATED.value());

        // then
        LineResponse response = 노선_조회_요청(이호선, OK.value()).as(LineResponse.class);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(1L, "강남역"),
                        tuple(2L, "선릉역"),
                        tuple(3L, "양재역")
                );
    }

    /**
     * WHEN 새로운 지하철 구간 생성시 상행역을 기준으로 노선의 가운데에 생성하면
     * Then 새로운 구간이 생성된다
     */
    @Test
    void 성공_새로운_지하철_구간_생성시_상행역을_기준으로_노선의_가운데에_생성할_수_있다() {
        // given
        SectionCreateRequest request = sectionCreateRequest(강남역_ID, 양재역_ID, 3);

        // when
        구간_생성_요청(request, CREATED.value());

        // then
        LineResponse response = 노선_조회_요청(이호선, OK.value()).as(LineResponse.class);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(1L, "강남역"),
                        tuple(2L, "선릉역"),
                        tuple(3L, "양재역")
                );
    }

    /**
     * WHEN 새로운 지하철 구간 생성시 하행역을 기준으로 노선의 가운데에 생성하면
     * Then 새로운 구간이 생성된다
     */
    @Test
    void 성공_새로운_지하철_구간_생성시_하행역을_기준으로_노선의_가운데에_생성할_수_있다() {
        // given
        SectionCreateRequest request = sectionCreateRequest(양재역_ID, 선릉역_ID, 3);

        // when
        구간_생성_요청(request, CREATED.value());

        // then
        LineResponse response = 노선_조회_요청(이호선, OK.value()).as(LineResponse.class);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(1L, "강남역"),
                        tuple(2L, "선릉역"),
                        tuple(3L, "양재역")
                );
    }

    /**
     * WHEN 새로운 지하철 구간 생성시 노선의 마지막에 생성하면
     * Then 새로운 구간이 생성된다
     */
    @Test
    void 성공_새로운_지하철_구간_생성시_노선의_마지막에_생성할_수_있다() {
        // given
        SectionCreateRequest request = sectionCreateRequest(선릉역_ID, 양재역_ID, 13);

        // when
        구간_생성_요청(request, CREATED.value());

        // then
        LineResponse response = 노선_조회_요청(이호선, OK.value()).as(LineResponse.class);
        assertThat(response.getStations()).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(1L, "강남역"),
                        tuple(2L, "선릉역"),
                        tuple(3L, "양재역")
                );
    }

    private SectionCreateRequest sectionCreateRequest(long upStationId, long downStationId, int distance) {
        return SectionCreateRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }

    private ExtractableResponse<Response> 구간_생성_요청(SectionCreateRequest request, int statusCode) {
        return post("/lines/{lineId}/sections", request, statusCode, 이호선);
    }

    /**
     * GIVEN 구간을 생성하고
     * WHEN 지하철 구간 제거시 마지막 구간이 아닐 경우
     * Then 구간을 제거할 수 없다
     */
    @Test
    void 실패_지하철_구간_제거시_마지막_구간이_아닐경우_구간을_제거할_수_없다() {
        // given
        SectionCreateRequest request = sectionCreateRequest(선릉역_ID, 양재역_ID, 13);
        post("/lines/{lineId}/sections", request, CREATED.value(), 이호선);

        // when
        String message = 구간_제거_요청(OK.value(), Map.of("stationId", "1"))
                .as(ExceptionResponse.class).getMessage();

        // then
        assertThat(message).isEqualTo("마지막 구간이 아닐 경우 구간을 제거할 수 없습니다.");
    }


    /**
     * WHEN 지하철 구간 제거시 구간이 한개만 있는 경우
     * Then 구간을 제거할 수 없다
     */
    @Test
    void 실패_지하철_구간_제거시_구간이_한개만_있는_경우_구간을_제거할_수_없다() {
        // when
        String message = 구간_제거_요청(OK.value(), Map.of("stationId", "1"))
                .as(ExceptionResponse.class).getMessage();

        // then
        assertThat(message).isEqualTo("구간이 한개만 있을 경우 구간을 제거할 수 없습니다.");
    }

    /**
     * GIVEN 구간을 생성하고
     * WHEN 지하철 구간 제거시
     * Then 구간을 제거한다
     */
    @Test
    void 성공_지하철_구간_제거시_구간의_제거에_성공한다() {
        // given
        SectionCreateRequest request = sectionCreateRequest(선릉역_ID, 양재역_ID, 13);
        post("/lines/{lineId}/sections", request, CREATED.value(), 이호선);

        // when
        구간_제거_요청(NO_CONTENT.value(), Map.of("stationId", "2"));

        // then
        LineResponse response = 노선_조회_요청(이호선, OK.value()).as(LineResponse.class);
        assertThat(response.getStations()).hasSize(2)
                .extracting("id", "name")
                .containsExactly(
                        tuple(1L, "강남역"),
                        tuple(2L, "선릉역")
                );
    }

    private ExtractableResponse<Response> 구간_제거_요청(int statusCode, Map<String, String> params) {
        return delete("/lines/{lineId}/sections", statusCode, params, 이호선);
    }

}
