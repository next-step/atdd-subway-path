package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static nextstep.subway.acceptance.model.LineEntitiesHelper.newLine;
import static nextstep.subway.acceptance.model.LineEntitiesHelper.노선_생성_요청;
import static nextstep.subway.acceptance.model.PathEntitiesHelper.최단_경로_조회_요청;
import static nextstep.subway.acceptance.model.PathEntitiesHelper.최단_경로_조회됨;
import static nextstep.subway.acceptance.model.SectionEntitiesHelper.newSection;
import static nextstep.subway.acceptance.model.SectionEntitiesHelper.구간_생성_요청;
import static nextstep.subway.acceptance.model.StationEntitiesHelper.강남역;
import static nextstep.subway.acceptance.model.StationEntitiesHelper.교대역;
import static nextstep.subway.acceptance.model.StationEntitiesHelper.남부터미널역;
import static nextstep.subway.acceptance.model.StationEntitiesHelper.양재역;
import static nextstep.subway.acceptance.model.StationEntitiesHelper.역삼역;
import static nextstep.subway.acceptance.model.StationEntitiesHelper.지하철역_생성_요청후_아이디_조회;
import static org.apache.http.HttpHeaders.LOCATION;

class PathAcceptanceTest extends AcceptanceTest {

    private static final String YANGJAE_TO_YEOKSAM = "/paths?source=5&target=3";
    private static final int EXPECTED_SIZE = 3;

    /**
     * 교대역(1) --- *2호선* ---   강남역(2) --- *2호선* ---  역삼역(3)
     * |                            |
     * *3호선*                   *신분당선*
     * |                             |
     * 남부터미널역(4)  --- *3호선* ---   양재(5)
     */
    @BeforeEach
    void setFixtures() {
        givens();
    }

    @Test
    void 최단_경로_조회() {
        ExtractableResponse<Response> response = 최단_경로_조회_요청(YANGJAE_TO_YEOKSAM);
        최단_경로_조회됨(response, EXPECTED_SIZE);
    }

    private void givens() {
        Long 교대역_ID = 지하철역_생성_요청후_아이디_조회(교대역);
        Long 강남역_ID = 지하철역_생성_요청후_아이디_조회(강남역);
        Long 역삼역_ID = 지하철역_생성_요청후_아이디_조회(역삼역);
        Long 남부터미널역_ID = 지하철역_생성_요청후_아이디_조회(남부터미널역);
        Long 양재역_ID = 지하철역_생성_요청후_아이디_조회(양재역);

        Map<String, Object> 신분당선 = newLine("신분당선", "bg-red-600", 강남역_ID, 양재역_ID, 5);
        Map<String, Object> 이호선 = newLine("이호선", "bg-green-600", 교대역_ID, 강남역_ID, 2);
        Map<String, Object> 삼호선 = newLine("삼호선", "bg-orange-600", 교대역_ID, 남부터미널역_ID, 3);

        노선_생성_요청(신분당선);
        ExtractableResponse<Response> 이호선_생성_응답 = 노선_생성_요청(이호선);
        ExtractableResponse<Response> 삼호선_생성_응답 = 노선_생성_요청(삼호선);

        구간_생성_요청(newSection(강남역_ID, 역삼역_ID, 4), createSectionCreateUri(이호선_생성_응답));
        구간_생성_요청(newSection(남부터미널역_ID, 양재역_ID, 3), createSectionCreateUri(삼호선_생성_응답));
    }

    private String createSectionCreateUri(ExtractableResponse<Response> response) {
        return response.header(LOCATION) + "/sections";
    }
}
