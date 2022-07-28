package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.SectionSteps.createLineCreateParams;
import static nextstep.subway.acceptance.SectionSteps.createSectionCreateParams;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능 실패 테스트")
class SectionAcceptanceFailTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 판교역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 판교역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 새로운 구간을 중간에 삽입할 때, 기존 구간의 길이보다 크거나 같으면
     * Then 노선에 새로운 구간이 추가되지 않는다.
     */
    @DisplayName("새로운 구간 삽입 시 기존 구간의 길이보다 크거나 같으면 새로운 구간이 추가되지 않는다.")
    @Test
    void addLineSectionWithUpStationFailOnLongerDistance() {
        // when
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        ExtractableResponse<Response> 구간의_길이가_클_때_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 15));
        ExtractableResponse<Response> 구간의_길이가_같을_때_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));

        // then
        assertThat(구간의_길이가_클_때_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(구간의_길이가_같을_때_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선에 구간을 등록한다.
     * When 새로운 구간을 추가 할 때, 기존 구간에 상행역과 하행역이 모두 포함되면
     * Then 노선에 구간이 추가되지 않는다.
     */
    @DisplayName("새로운 구간 등록 시, 먼저 상행역과 하행역이 구간으로 이미 등록되어 있으면 실패한다.")
    @Test
    void addLineSectionFailWithAlreadyAdded() {
        // given
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신사역, 강남역, 3));

        // when
        ExtractableResponse<Response> 구간_생성_요청_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신사역, 양재역, 3));

        // then
        assertThat(구간_생성_요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선에 구간을 등록한다.
     * When 새로운 구간을 추가 할 때, 기존 구간에 상행역과 하행역이 모두 등록되어있지 않으면
     * Then 노선에 구간이 추가되지 않는다.
     */
    @DisplayName("새로운 구간 등록 시, 상행역과 하행역 모두 등록되어 있지 않으면 실패한다.")
    @Test
    void addLineSectionFailWithoutAlreadyRegisteredStation() {
        // given
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 3));
        Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
        Long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> 구간_생성_요청_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신사역, 논현역, 5));

        // then
        assertThat(구간_생성_요청_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 구간이 1개인 지하철 노선의 제거를 요청 하면
     * Then BAD_REQUEST가 반환된다.
     */
    @DisplayName("구간이 하나인 노선의 구간을 삭제하려면 실패한다.")
    @Test
    void removeLineSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선에 등록되어있지 않은 역으로 구간을 제거하려하면
     * Then 오류가 발생한다.
     */
    @DisplayName("구간이 하나인 노선의 구간을 삭제하려면 실패한다.")
    @Test
    void removeSectionWithNotRegisteredStation() {
        // given
        long 등록되어_있지_않은_역 = 지하철역_생성_요청("등록되어 있지 않은 역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(신분당선, 등록되어_있지_않은_역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
