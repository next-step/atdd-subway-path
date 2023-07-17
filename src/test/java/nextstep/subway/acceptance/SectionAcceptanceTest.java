package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.steps.LineSteps.*;
import static nextstep.subway.acceptance.steps.StationSteps.역_생성_ID_추출;
import static nextstep.subway.acceptance.steps.StationSteps.지하철역_생성_요청;
import static nextstep.subway.fixture.LineFixture.DISTANCE_10;
import static nextstep.subway.fixture.LineFixture.분당선;
import static nextstep.subway.fixture.StationFixture.*;
import static nextstep.subway.utils.CustomAssertions.상태코드_확인;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    Long STATION_ID_1;
    Long STATION_ID_2;
    Long STATION_ID_3;
    Long LINE_ID_1;


    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        STATION_ID_1 = 역_생성_ID_추출(지하철역_생성_요청(강남역));
        STATION_ID_2 = 역_생성_ID_추출(지하철역_생성_요청(역삼역));
        STATION_ID_3 = 역_생성_ID_추출(지하철역_생성_요청(선릉역));

        LINE_ID_1 = 노선_생성_ID_추출(지하철_노선_생성_요청(분당선, 강남역, STATION_ID_1, STATION_ID_2, DISTANCE_10));
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철_구간_생성_요청(LINE_ID_1, STATION_ID_2, STATION_ID_3, DISTANCE_10);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(LINE_ID_1);
        상태코드_확인(response, HttpStatus.OK);
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(STATION_ID_1, STATION_ID_2, STATION_ID_3);
    }

//    /**
//     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
//     * When 지하철 노선의 마지막 구간 제거를 요청 하면
//     * Then 노선에 구간이 제거된다
//     */
//    @DisplayName("지하철 노선에 구간을 제거")
//    @Test
//    void removeLineSection() {
//        // given
//        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
//        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역));
//
//        // when
//        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);
//
//        // then
//        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(강남역,
//                양재역);
//    }
//
//    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
//        Map<String, String> lineCreateParams;
//        lineCreateParams = new HashMap<>();
//        lineCreateParams.put("name", "신분당선");
//        lineCreateParams.put("color", "bg-red-600");
//        lineCreateParams.put("upStationId", upStationId + "");
//        lineCreateParams.put("downStationId", downStationId + "");
//        lineCreateParams.put("distance", 10 + "");
//        return lineCreateParams;
//    }
//
//    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
//        Map<String, String> params = new HashMap<>();
//        params.put("upStationId", upStationId + "");
//        params.put("downStationId", downStationId + "");
//        params.put("distance", 6 + "");
//        return params;
//    }
}
