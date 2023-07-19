package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.PathSteps.경로_조회_요청;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선 (10)* ---   강남역
     * |                              |
     * *3호선 (2)*                  *신분당선 (10)*
     * |                              |
     * 남부터미널역  --- *3호선 (3)* ---  양재역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = ID_추출(지하철역_생성_요청("교대역"));
        강남역 = ID_추출(지하철역_생성_요청("강남역"));
        양재역 = ID_추출(지하철역_생성_요청("양재역"));
        남부터미널역 = ID_추출(지하철역_생성_요청("남부터미널역"));

        이호선 = ID_추출(지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10));
        신분당선 = ID_추출(지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10));
        삼호선 = ID_추출(지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2));

        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    @Test
    void searchPath() {

        // when
        ExtractableResponse<Response> response = 경로_조회_요청(강남역, 남부터미널역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<StationTestResponse> stations = response.jsonPath().getList("stations", StationTestResponse.class);
        assertThat(stations).hasSize(3);
        assertThat(stations.stream().map(StationTestResponse::getId).collect(Collectors.toList())).containsExactly(강남역, 교대역, 남부터미널역);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }

    private Long ID_추출(ExtractableResponse<Response> lineCreationResponse) {
        return lineCreationResponse.jsonPath().getLong("id");
    }

    private static class StationTestResponse {
        private Long id;

        public Long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            StationTestResponse that = (StationTestResponse) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
