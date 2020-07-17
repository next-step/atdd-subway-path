package nextstep.subway.path;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.*;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.*;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.ShortestPathResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("2단계 지하철 경로 조회 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 아이디_우이신설선;
    private Long 아이디_6호선;
    private Long 아이디_창신역;
    private Long 아이디_보문역;
    private Long 아이디_안암역;
    private Long 아이디_고려대역;
    private Long 아이디_월곡역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 등록응답_우이신설선 = 지하철_노선_등록되어_있음("우이신설선", "GREEN");
        ExtractableResponse<Response> 등록응답_6호선 = 지하철_노선_등록되어_있음("6호선", "BROWN");

        ExtractableResponse<Response> 등록응답_창신역 = 지하철역_등록되어_있음("창신역");
        ExtractableResponse<Response> 등록응답_보문역 = 지하철역_등록되어_있음("보문역");
        ExtractableResponse<Response> 등록응답_안암역 = 지하철역_등록되어_있음("안암역");
        ExtractableResponse<Response> 등록응답_고려대역 = 지하철역_등록되어_있음("고려대역");
        ExtractableResponse<Response> 등록응답_월곡역 = 지하철역_등록되어_있음("월곡역");

        아이디_우이신설선 = 등록응답_우이신설선.as(LineResponse.class).getId();
        아이디_6호선 = 등록응답_6호선.as(LineResponse.class).getId();

        아이디_창신역 = 등록응답_창신역.as(StationResponse.class).getId();
        아이디_보문역 = 등록응답_보문역.as(StationResponse.class).getId();
        아이디_안암역 = 등록응답_안암역.as(StationResponse.class).getId();
        아이디_고려대역 = 등록응답_고려대역.as(StationResponse.class).getId();
        아이디_월곡역 = 등록응답_월곡역.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(아이디_우이신설선, null, 아이디_창신역);
        지하철_노선에_지하철역_등록되어_있음(아이디_우이신설선, 아이디_창신역, 아이디_보문역);
        지하철_노선에_지하철역_등록되어_있음(아이디_우이신설선, 아이디_보문역, 아이디_안암역);

        지하철_노선에_지하철역_등록되어_있음(아이디_6호선, null, 아이디_창신역);
        지하철_노선에_지하철역_등록되어_있음(아이디_6호선, 아이디_창신역, 아이디_고려대역);
        지하철_노선에_지하철역_등록되어_있음(아이디_6호선, 아이디_고려대역, 아이디_월곡역);
    }

    @DisplayName("두 역 간의 최단 거리 경로를 조회한다.")
    @Test
    void 출발역과_도착역의_최단거리_경로를_조회한다() {

        // given: 지하철 역, 지하철 노선, 지하철 노선에 지하철 역 등록 작업이 사전에 이루어진다.

        // when: 출발역에서 도착역까지의 최단거리 경로 조회를 요청한다.
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .queryParam("source", 1)
            .queryParam("target", 6)
            .get("/paths")
            .then().log().all().extract();

        // then: 최단 거리 경로를 응답한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // and: 총 거리와 소요 시간을 함께 응답한다.
        ShortestPathResponse pathResponse = response.as(ShortestPathResponse.class);
        assertThat(pathResponse.getDuration()).isNotNull();
        assertThat(pathResponse.getDistance()).isNotNull();
    }
}
