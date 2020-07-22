package nextstep.subway.path.acceptance;

import static nextstep.subway.line.acceptance.step.LineAcceptanceStep.*;
import static nextstep.subway.line.acceptance.step.LineStationAcceptanceStep.*;
import static nextstep.subway.station.acceptance.step.StationAcceptanceStep.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("2단계 지하철 경로 조회 인수 테스트")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 아이디_신분당선;
    private Long 아이디_2호선;
    private Long 아이디_강남역;
    private Long 아이디_역삼역;
    private Long 아이디_선릉역;
    private Long 아이디_양재역;
    private Long 아이디_양재시민의숲역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 등록응답_신분당선 = 지하철_노선_등록되어_있음("신분당선", "RED");
        ExtractableResponse<Response> 등록응답_2호선 = 지하철_노선_등록되어_있음("2호선", "GREEN");

        ExtractableResponse<Response> 등록응답_강남역 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> 등록응답_역삼역 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> 등록응답_선릉역 = 지하철역_등록되어_있음("선릉역");
        ExtractableResponse<Response> 등록응답_양재역 = 지하철역_등록되어_있음("양재역");
        ExtractableResponse<Response> 등록응답_양재시민의숲역 = 지하철역_등록되어_있음("양재시민의숲역");

        아이디_신분당선 = 등록응답_신분당선.as(LineResponse.class).getId();
        아이디_2호선 = 등록응답_2호선.as(LineResponse.class).getId();

        아이디_강남역 = 등록응답_강남역.as(StationResponse.class).getId();
        아이디_역삼역 = 등록응답_역삼역.as(StationResponse.class).getId();
        아이디_선릉역 = 등록응답_선릉역.as(StationResponse.class).getId();
        아이디_양재역 = 등록응답_양재역.as(StationResponse.class).getId();
        아이디_양재시민의숲역 = 등록응답_양재시민의숲역.as(StationResponse.class).getId();

        지하철_노선에_지하철역_등록되어_있음(아이디_2호선, null, 아이디_강남역);
        지하철_노선에_지하철역_등록되어_있음(아이디_2호선, 아이디_강남역, 아이디_역삼역);
        지하철_노선에_지하철역_등록되어_있음(아이디_2호선, 아이디_역삼역, 아이디_선릉역);

        지하철_노선에_지하철역_등록되어_있음(아이디_신분당선, null, 아이디_강남역);
        지하철_노선에_지하철역_등록되어_있음(아이디_신분당선, 아이디_강남역, 아이디_양재역);
        지하철_노선에_지하철역_등록되어_있음(아이디_신분당선, 아이디_양재역, 아이디_양재시민의숲역);
    }

    @DisplayName("두 역 간의 최단 거리 경로를 조회한다.")
    @Test
    void 출발역과_도착역의_최단거리_경로를_조회한다() {

        // given: 지하철 역, 지하철 노선, 지하철 노선에 지하철 역 등록 작업이 사전에 이루어진다.

        // when: 출발역에서 도착역까지의 최단거리 경로 조회를 요청한다.
        ExtractableResponse<Response> response =
            PathAcceptanceStep.출발역에서_도착역까지_최단경로를_조회한다(아이디_선릉역, 아이디_양재시민의숲역, "DISTANCE");

        // then: 최단 거리 경로를 응답한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // and: 총 거리와 소요 시간을 함께 응답한다.
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDuration()).isNotNull();
        assertThat(pathResponse.getDistance()).isNotNull();
        assertThat(pathResponse.getDuration()).isEqualTo(8);
    }

    @DisplayName("출발역에서 도착역까지의 최소 시간 경로를 조회할 수 있다.")
    @Test
    void 출발역과_도착역_사이의_최소시간_경로를_조회한다() {
        // given: 지하철역, 지하철 노선, 지하철 노선에 지하철 역 등록 작업이 사전에 이루어진다.

        // when: 출발역에서 도착역까지의 최소시간 경로 조회를 요청한다.
        ExtractableResponse<Response> response =
            PathAcceptanceStep.출발역에서_도착역까지_최단경로를_조회한다(아이디_선릉역, 아이디_양재시민의숲역, "DURATION");

        // then: 최소 시간 경로를 응답한다.
        PathResponse pathResponse = response.as(PathResponse.class);
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(pathResponse.getStationResponses()).isNotEmpty()
        );

        // and: 총 거리와 소요 시간을 함께 응답한다.
        assertThat(pathResponse.getDuration()).isNotNull();
        assertThat(pathResponse.getDistance()).isNotNull();
        assertThat(pathResponse.getDuration()).isEqualTo(8);
    }
}
