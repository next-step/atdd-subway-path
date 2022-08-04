package nextstep.subway.acceptance;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.client.LineClient;
import nextstep.subway.client.PathClient;
import nextstep.subway.client.StationClient;
import nextstep.subway.client.dto.LineCreationRequest;
import nextstep.subway.client.dto.SectionRegistrationRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Lines;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Paths;
import nextstep.subway.exception.NoPathException;
import nextstep.subway.exception.SectionRemovalException;
import nextstep.subway.utils.GivenUtils;
import nextstep.subway.utils.HttpStatusValidator;
import nextstep.subway.utils.JsonResponseConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.NoSuchElementException;

import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.TEN;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.분당선;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.이호선;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("지하철 경로 검색")
class PathsAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    @Autowired
    private JsonResponseConverter responseConverter;

    @Autowired
    private GivenUtils givenUtils;

    @Autowired
    private LineClient lineClient;

    @Autowired
    private StationClient stationClient;

    @Autowired
    private PathClient pathClient;

    @Autowired
    private HttpStatusValidator statusValidator;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = responseConverter.convertToId(givenUtils.교대역_생성());
        강남역 = responseConverter.convertToId(givenUtils.강남역_생성());
        양재역 = responseConverter.convertToId(givenUtils.양재역_생성());
        남부터미널역 = responseConverter.convertToId(givenUtils.남부터미널역_생성());

        이호선 = responseConverter.convertToId(
                givenUtils.지하철_생성("2호선", "green", 교대역, 강남역, TEN));
        신분당선 = responseConverter.convertToId(
                givenUtils.지하철_생성("신분당선", "red", 강남역, 양재역, TEN));
        삼호선 = responseConverter.convertToId(
                givenUtils.지하철_생성("3호선", "orange", 교대역, 남부터미널역, FIVE));

        lineClient.addSection(삼호선, new SectionRegistrationRequest(남부터미널역, 양재역, FIVE));
    }

    /**
     * Given 노선을 생성 후
     * When 최단경로를 조회하면
     * Then 최단경로의 역과 거리가 조회된다
     */
    @Test
    @DisplayName("최단경로 조회")
    @DirtiesContext
    void getShortestPath() {
        // given
        int expectedSize = 3;
        int expectedDistance = 10;

        // when
        ExtractableResponse<Response> response = pathClient.getShortestPath(교대역, 양재역);


        // then
        statusValidator.validateOk(response);
        assertThat(responseConverter.convert(response, "stations", List.class)).hasSize(expectedSize);
        assertThat(responseConverter.convert(response, "distance", Long.class)).isEqualTo(expectedDistance);
    }

    /**
     * Given 노선을 생성 후
     * When 최단경로를 존재하지 않는 역으로 조회하면
     * Then 오류(NoSuchElementException) 객체를 반환한다
     */
    @Test
    @DisplayName("최단경로 조회 실패 - 존재하지 않는역 조회")
    @DirtiesContext
    void getShortestPathWithInvalidStationId() {
        // given
        Long nonExistId = 99L;

        // when
        ExtractableResponse<Response> response = pathClient.getShortestPath(nonExistId, 양재역);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(NoSuchElementException.class.getName());
    }

    /**
     * Given 노선을 생성 후
     * When 최단경로를 출발역, 도착역을 같은 역으로 조회하면
     * Then 오류(IllegalArgumentException) 객체를 반환한다
     */
    @Test
    @DisplayName("최단경로 조회 실패 - 출발역, 도착역 같은 경우")
    @DirtiesContext
    void getShortestPathWithSameStationIds() {
        // given

        // when
        ExtractableResponse<Response> response = pathClient.getShortestPath(양재역, 양재역);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(IllegalArgumentException.class.getName());
    }

    /**
     * Given 노선을 생성 후
     * When 최단경로를 출발역, 도착역을 같은 역으로 조회하면
     * Then 오류(NoPathException) 객체를 반환한다
     */
    @Test
    @DisplayName("최단경로 조회 실패 - 출발역, 도착역이 연결되지 않은 경우")
    @DirtiesContext
    void getShortestPathWithDisconnected() {
        // given
        Long 동떨어진역 = responseConverter.convertToId(stationClient.createStation("동떨어진역"));
        Long 동떨어진역2 = responseConverter.convertToId(stationClient.createStation("동떨어진역2"));
        lineClient.createLine(new LineCreationRequest("동떨어진라인", "black", 동떨어진역, 동떨어진역2, TEN));

        // when
        ExtractableResponse<Response> response = pathClient.getShortestPath(동떨어진역, 강남역);

        // then
        statusValidator.validateBadRequest(response);
        assertThat(responseConverter.convertToError(response))
                .contains(NoPathException.class.getName());
    }

}