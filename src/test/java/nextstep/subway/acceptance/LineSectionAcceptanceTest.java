package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청한다;
import static nextstep.subway.common.LineSomething.DISTANCE_BASIC;
import static nextstep.subway.common.LineSomething.DISTANCE_INVALID;
import static nextstep.subway.common.LineSomething.DISTANCE_VALID;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 일번역1;
    private Long 이번역2;
    private Long 삼번역3;
    private Long 사번역4;
    private Long 신규역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        일번역1 = 지하철역_생성_요청한다(StationRequest.of("일번역1")).as(StationResponse.class).getId();
        이번역2 = 지하철역_생성_요청한다(StationRequest.of("이번역2")).as(StationResponse.class).getId();
        삼번역3 = 지하철역_생성_요청한다(StationRequest.of("삼번역3")).as(StationResponse.class).getId();
        사번역4 = 지하철역_생성_요청한다(StationRequest.of("사번역4")).as(StationResponse.class).getId();

        신분당선 = 지하철_노선_생성_요청(LineRequest.of("신분당선", "bg-red-600", 일번역1, 이번역2, DISTANCE_BASIC))
            .jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(이번역2, 삼번역3, DISTANCE_BASIC));
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(삼번역3, 사번역4, DISTANCE_BASIC));

        신규역 = 지하철역_생성_요청한다(StationRequest.of("신규역")).as(StationResponse.class).getId();
    }

    @DisplayName("기존 지하철 노선 뒤에 구간 추가 성공하는 단순 케이스 (해피케이스)")
    @Test
    void addLineSection_성공케이스_해피케이스() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(사번역4, 신규역, DISTANCE_VALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(일번역1, 이번역2, 삼번역3, 사번역4, 신규역);
    }

    @DisplayName("지하철 노선에 상행선쪽에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_1() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(신규역, 이번역2, DISTANCE_VALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(일번역1, 신규역, 이번역2, 삼번역3, 사번역4);
    }

    @DisplayName("지하철 노선에 있는 구간들 중 상행역 쪽 중간역에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_2() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(이번역2, 신규역, DISTANCE_VALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(일번역1, 이번역2, 신규역, 삼번역3, 사번역4);
    }

    @DisplayName("지하철 노선에 있는 구간들 중 하행역 쪽 중간역에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_3() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(신규역, 삼번역3, DISTANCE_VALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(일번역1, 이번역2, 신규역, 삼번역3, 사번역4);
    }

    @DisplayName("지하철 노선에 하행선쪽에 구간 등록을 성공")
    @Test
    void addLineSection_성공케이스_4() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(삼번역3, 신규역, DISTANCE_VALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(일번역1, 이번역2, 삼번역3, 신규역, 사번역4);
    }

    @DisplayName("지하철 노선에 있는 구간 사이에 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 실패")
    @Test
    void addLineSection_실패케이스_1() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(이번역2, 신규역, DISTANCE_INVALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간을 등록 시 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 등록 실패")
    @Test
    void addLineSection_실패케이스_2() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(신규역, 신규역, DISTANCE_VALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간을 등록 시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 등록 실패")
    @Test
    void addLineSection_실패케이스_3() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(일번역1, 사번역4, DISTANCE_VALID));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철_구간_생성_요청(신분당선, SectionRequest.of(사번역4, 신규역, DISTANCE_VALID));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 신규역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class))
            .containsExactly(일번역1, 이번역2, 삼번역3, 사번역4);
    }

}
