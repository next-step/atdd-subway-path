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

        지하철_노선에_지하철_구간_생성_요청(일호선, createSectionCreateParams(구일역, 구로역, DEFAULT_SECTION_DISTANCE));
    }

    //When 구일역 구간 제거를 요청하면.
    //Then 개봉역 - 구일역 - 구로역 -신도림 -> 개봉역 - 구로역 -신도림 의 구간으로 제배치
    @DisplayName("지하철 구간을 삭제한다.(중간역)")
    @Test
    void deleteSection() {
        Long 신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(일호선, createSectionCreateParams(구로역, 신도림역, DEFAULT_SECTION_DISTANCE));
        // when
        지하철_노선에_지하철_구간_제거_요청(일호선, 구일역);
        // then
        ExtractableResponse<Response> stationResponse = 지하철_노선_조회_요청(일호선);
        ExtractableResponse<Response> sectionResponse = 지하철_노선의_구간목록_조회_요청(일호선);

        assertAll(
                () -> assertThat(stationResponse.jsonPath().getList("stations.name",String.class)).containsExactly("개봉역", "구로역","신도림역"),
                () -> assertThat(sectionResponse.jsonPath().getList("distance", Integer.class)).containsExactly(11,4)
        );
    }

    //When 구로역 구간 제거를 요청하면.
    //Then 개봉역 - 구일역 - 구로역 -> 구일역 - 구로역의 구간 제거.
    @DisplayName("지하철 구간을 삭제한다.(종점역)")
    @Test
    void deleteSection2() {
        // when
        지하철_노선에_지하철_구간_제거_요청(일호선, 구로역);
        // then
        ExtractableResponse<Response> stationResponse = 지하철_노선_조회_요청(일호선);
        ExtractableResponse<Response> sectionResponse = 지하철_노선의_구간목록_조회_요청(일호선);

        assertAll(
                () -> assertThat(stationResponse.jsonPath().getList("stations.name",String.class)).containsExactly("개봉역", "구일역"),
                () -> assertThat(sectionResponse.jsonPath().getList("distance", Integer.class)).containsExactly(7)
        );
    }

    //When 개봉역 구간 제거를 요청하면.
    //Then 개봉역 - 구일역 - 구로역 -> 개봉역 - 구일역의 구간 제거.
    @DisplayName("지하철 구간을 삭제한다.(상행역)")
    @Test
    void deleteSection3() {
        // when
        지하철_노선에_지하철_구간_제거_요청(일호선, 개봉역);
        // then
        ExtractableResponse<Response> stationResponse = 지하철_노선_조회_요청(일호선);
        ExtractableResponse<Response> sectionResponse = 지하철_노선의_구간목록_조회_요청(일호선);

        assertAll(
                () -> assertThat(stationResponse.jsonPath().getList("stations.name",String.class)).containsExactly("구일역", "구로역"),
                () -> assertThat(sectionResponse.jsonPath().getList("distance", Integer.class)).containsExactly(4)
        );
    }

    //When 구간이 하나 남았을때 삭제 요청을 하면.
    //Then 제거 실패한다.
    @DisplayName("지하철 구간을 삭제를 실패한다.")
    @Test
    void deleteSectionFail() {
        지하철_노선에_지하철_구간_제거_요청(일호선, 구로역);
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(일호선, 구일역);
        // then
        BADREQUEST_실패케이스_검증("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.",response,HttpStatus.BAD_REQUEST);

    }

    //When 노선에 등록되어 있지 않은 지하철역 삭제.
    //Then 제거 실패한다.
    @DisplayName("지하철 구간을 삭제를 실패한다 - 노선 구간에 등록되어 있지 않은 지하철역 요청..")
    @Test
    void deleteSectionFail2() {
        Long 신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(일호선, 신도림역);
        // then
        BADREQUEST_실패케이스_검증("노선에 존재하지 않는 지하철역 입니다.",response, HttpStatus.NOT_FOUND);

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
