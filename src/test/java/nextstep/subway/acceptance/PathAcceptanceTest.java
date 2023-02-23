package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.acceptance.LineSectionAcceptanceTest.createSectionCreateParams;
import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 검색")
class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* (10) ---   강남역
     *    |                           |
     * *3호선* (2)                  *신분당선* (10)
     *    |                           |
     * 남부터미널역  --- *3호선* (3)---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

        이호선 = 지하철_노선_생성_요청("2호선", "green").jsonPath().getLong("id");
        신분당선 = 지하철_노선_생성_요청("신분당선", "red").jsonPath().getLong("id");
        삼호선 = 지하철_노선_생성_요청("3호선", "orange").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역, 10));
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 10));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(교대역, 남부터미널역, 2));
        지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
    }

    @Test
    @DisplayName("최단 거리를 조회한다.")
    void paths_When_두_역_사이의_최단_경로를_조회하면_Then_최단_경로를_조회한다() {
        //when
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 양재역);
        var pathResponse = response.as(PathResponse.class);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(pathResponse.getStationResponse()).hasSize(3);
        assertThat(pathResponse.getStationResponse().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactlyElementsOf(List.of("교대역", "남부터미널역", "양재역"));
        assertThat(pathResponse.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("최단 거리를 조회한다.")
    void paths_Given_출발역과_도착역이_같은_경우_When_두_역_사이의_최단_경로를_조회하면_Then_BadRequest() {
        //when
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 교대역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.jsonPath().getObject(".", ErrorResponse.class);
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo(CustomException.SAME_STATION_CAN_NOT_SEARCH_PATH);
    }

    @Test
    @DisplayName("최단 거리를 조회한다.")
    void paths_Given_존재하지_않은_출발역이나_도착역을_조회_할_경우_When_두_역_사이의_최단_경로를_조회하면_Then_BadRequest() {
        //given
        Long invalidStationId = 100L;

        //when
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, invalidStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.jsonPath().getObject(".", ErrorResponse.class);
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo(CustomException.PATH_MUST_CONTAIN_STATION);
    }

    @Test
    @DisplayName("최단 거리를 조회한다.")
    void paths_Given_출발역과_도착역이_연결이_되어_있지_않은_경우_When_두_역_사이의_최단_경로를_조회하면_Then_BadRequest() {
        //given
        Long 뚝섬역 = 지하철역_생성_요청("뚝섬역").jsonPath().getLong("id");
        Long 성수역 = 지하철역_생성_요청("성수역").jsonPath().getLong("id");
        Long 칠호선 = 지하철_노선_생성_요청("7호선", "yellow").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(칠호선, createSectionCreateParams(뚝섬역, 성수역, 10));

        //when
        ExtractableResponse<Response> response = 경로_조회_요청(교대역, 뚝섬역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.jsonPath().getObject(".", ErrorResponse.class);
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo(CustomException.DOES_NOT_CONNECTED_SOURCE_TO_TARGET);
    }
}
