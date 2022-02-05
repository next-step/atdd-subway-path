package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import org.assertj.core.api.AbstractIntegerAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    private Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 아이디_획득(지하철역_생성_요청("강남역"));
        양재역 = 아이디_획득(지하철역_생성_요청("양재역"));
        정자역 = 아이디_획득(지하철역_생성_요청("정자역"));

        신분당선 = 아이디_획득(지하철_노선_생성_요청(createLineCreateParams(강남역,
                                                          양재역)));
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 - 강남->양재->정자")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선,
                             createSectionCreateParams(양재역,
                                                       정자역));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        정상_응답200(response);
        노선의_역_목록이_출력된다(response,
                       강남역,
                       양재역,
                       정자역);
    }

    @DisplayName("구간 추가 - 상행역이 같은 경우에 새로운 역을 등록 [강남-양재, 강남-정자]")
    @Test
    void addLineSection2() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선,
                             createSectionCreateParams(강남역,
                                                       정자역,
                                                       4));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        노선의_역_목록이_출력된다(response,
                       강남역,
                       정자역,
                       양재역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 [강남-양재, 양재-정자]")
    @Test
    void addLineSection3() {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선,
                             createSectionCreateParams(양재역,
                                                       정자역,
                                                       3));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        노선의_역_목록이_출력된다(response,
                       강남역,
                       양재역,
                       정자역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크면 등록할 수 없음 [강남-양재, 정자-양재]")
    @Test
    void addLineSection4() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                                                                      createSectionCreateParams(정자역,
                                                                                                양재역,
                                                                                                8));

        // then
        BAD_REQUEST_응답을_한다(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 구간과 직접적으로 동일하다면 등록할 수 없음 [강남-양재, 강남-양재]")
    @Test
    void addLineSection5() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                                                                      createSectionCreateParams(강남역,
                                                                                                양재역,
                                                                                                7));

        // then
        BAD_REQUEST_응답을_한다(response);
    }


    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 구간과 간접적으로 동일하다면 등록할 수 없음 [강남-양재-정자, 강남-정자]")
    @Test
    void addLineSection6() {
        지하철_노선에_지하철_구간_생성_요청(신분당선,
                             createSectionCreateParams(양재역,
                                                       정자역,
                                                       10));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                                                                      createSectionCreateParams(강남역,
                                                                                                정자역,
                                                                                                10));


        // then
        BAD_REQUEST_응답을_한다(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음 [강남-양재-정자, 새로운역1-새로운역2]")
    @Test
    void addLineSection7() {
        지하철_노선에_지하철_구간_생성_요청(신분당선,
                             createSectionCreateParams(양재역,
                                                       정자역,
                                                       10));

        Long 새로운역1 = 지하철역_생성_요청("새로운역1").jsonPath()
                                        .getLong("id");
        Long 새로운역2 = 지하철역_생성_요청("새로운역2").jsonPath()
                                        .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선,
                                                                      createSectionCreateParams(새로운역1,
                                                                                                새로운역2,
                                                                                                10));

        // then
        BAD_REQUEST_응답을_한다(response);
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
        지하철_노선에_지하철_구간_생성_요청(신분당선,
                             createSectionCreateParams(양재역,
                                                       정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선,
                             정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        정상_응답200(response);
        노선의_역_목록이_출력된다(response,
                       강남역,
                       양재역);
    }

    private LineRequest createLineCreateParams(Long upStationId, Long downStationId) {
        return new LineRequest("신분당선",
                               "bg-red-600",
                               upStationId,
                               downStationId,
                               7);
    }

    private SectionRequest createSectionCreateParams(Long upStationId, Long downStationId) {
        return new SectionRequest(upStationId,
                                  downStationId,
                                  6);
    }

    private SectionRequest createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        return new SectionRequest(upStationId,
                                  downStationId,
                                  distance);
    }

    private Long 아이디_획득(ExtractableResponse<Response> response) {
        return response.jsonPath()
                       .getLong("id");
    }

    private AbstractIntegerAssert<?> BAD_REQUEST_응답을_한다(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 노선의_역_목록이_출력된다(ExtractableResponse<Response> response, Long... 기대하는_역목록) {
        List<Long> 실제_역목록 = response.jsonPath()
                                    .getList("stations.id",
                                             Long.class);
        assertThat(실제_역목록).containsExactly(기대하는_역목록);
    }

    private void 정상_응답200(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
