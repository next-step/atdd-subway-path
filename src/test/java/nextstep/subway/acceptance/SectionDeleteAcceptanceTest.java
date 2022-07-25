package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.BADREQUEST_실패케이스_검증;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static nextstep.subway.acceptance.SectionSteps.지하철_노선의_구간목록_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관리 기능")
class SectionDeleteAcceptanceTest extends AcceptanceTest {
    private Long 일호선;

    private Long 개봉역;
    private Long 구일역;
    private Long 구로역;

    private final int DEFAULT_SECTION_DISTANCE = 4;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        개봉역 = 지하철역_생성_요청("개봉역").jsonPath().getLong("id");
        구일역 = 지하철역_생성_요청("구일역").jsonPath().getLong("id");
        구로역 = 지하철역_생성_요청("구로역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(개봉역, 구일역);
        일호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");

//        지하철_노선에_지하철_구간_생성_요청(일호선, createSectionCreateParams(개봉역, 구일역, DEFAULT_SECTION_DISTANCE));
        지하철_노선에_지하철_구간_생성_요청(일호선, createSectionCreateParams(구일역, 구로역, DEFAULT_SECTION_DISTANCE));

    }

    //When 구일역 구간 제거를 요청하면.
    //Then 개봉역 - 구일역 - 구로역 -> 개봉역 - 구로역 의 구간으로 제배치
    @DisplayName("지하철 구간을 삭제한다.(중간역)")
    @Test
    void deleteSection() {
        // when

        // then
        지하철_노선_조회_요청(일호선);
        지하철_노선의_구간목록_조회_요청(일호선);
    }

    //When 구로역 구간 제거를 요청하면.
    //Then 개봉역 - 구일역 - 구로역 -> 구일역 - 구로역의 구간 제거.
    @DisplayName("지하철 구간을 삭제한다.(종점역)")
    @Test
    void deleteSection2() {
        // when

        // then
        지하철_노선_조회_요청(일호선);
        지하철_노선의_구간목록_조회_요청(일호선);
    }

    //When 개봉역 구간 제거를 요청하면.
    //Then 개봉역 - 구일역 - 구로역 -> 개봉역 - 구일역의 구간 제거.
    @DisplayName("지하철 구간을 삭제한다.(상행역)")
    @Test
    void deleteSection3() {
        // when

        // then
        지하철_노선_조회_요청(일호선);
        지하철_노선의_구간목록_조회_요청(일호선);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "일호선");
        lineCreateParams.put("color", "blue");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 7 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
