package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.AssertUtils;
import nextstep.subway.utils.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = ResponseUtils.getLong(지하철역_생성_요청("강남역"), "id");
        양재역 = ResponseUtils.getLong(지하철역_생성_요청("양재역"), "id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = ResponseUtils.getLong(지하철_노선_생성_요청(lineCreateParams), "id");
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
        Long 정자역 = ResponseUtils.getLong(지하철역_생성_요청("정자역"), "id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        AssertUtils.lineSection(response, 강남역, 양재역);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        return Map.of(
                "name", "신분당선",
                "color", "bg-red-600",
                "upStationId", upStationId.toString(),
                "downStationId", downStationId.toString(),
                "distance", "10"
        );
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        return Map.of(
                "upStationId", upStationId.toString(),
                "downStationId", downStationId.toString(),
                "distance", "6"
        );
    }
}
