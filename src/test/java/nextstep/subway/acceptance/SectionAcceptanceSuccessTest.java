package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.ListAssert;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceSuccessTest extends AcceptanceTest {
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
     * When downStation으로 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("DownStation으로 지하철 노선에 구간을 등록")
    @Test
    void addLineSectionWithDownStation() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 6));

        // then
        조회_결과를_검증한다(신분당선, 강남역, 판교역, 정자역);
    }

    /**
     * When upStation으로 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("upStation으로 지하철 노선에 구간을 등록")
    @Test
    void addLineSectionWithUpStation() {
        // when
        Long 양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 6));

        // then
        조회_결과를_검증한다(신분당선, 강남역, 양재역, 판교역);
    }

    /**
     * When 기존 구간의 upStation과 일치하는 downStation을 가진 구간이 추가 요청이 되면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("새로운 구간의 downStation이 기존 구간의 upStation과 일치하는 경우 지하철 노선에 등록된다.")
    @Test
    void addLineSectionWithSameDownStation() {
        // when
        Long 신사역 = 지하철역_생성_요청("신사역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신사역, 강남역, 6));

        // then
        조회_결과를_검증한다(신분당선, 신사역, 강남역, 판교역);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역, 판교역);

        조회_결과를_검증한다(신분당선, 강남역, 판교역);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("노선 중간에 있는 역을 제거할 수 있다.")
    @Test
    void removeLineSectionInTheMiddle() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(판교역, 정자역, 6));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

        // then
        조회_결과를_검증한다(신분당선, 강남역, 정자역);
    }

    private void 조회_결과를_검증한다(Long lineId, Long... subwayId) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);
        assertAll(
                () -> 상태코드가_OK임을_확인한다(response),
                () -> 반환되는_역에_해당_역들이_있는지_확인한다(response, subwayId)
        );
    }

    private AbstractIntegerAssert<?> 상태코드가_OK임을_확인한다(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ListAssert<Long> 반환되는_역에_해당_역들이_있는지_확인한다(ExtractableResponse<Response> response, Long[] subwayId) {
        return assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(subwayId);
    }
}
