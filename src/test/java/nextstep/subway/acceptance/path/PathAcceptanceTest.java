package nextstep.subway.acceptance.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step.LineAcceptanceStep;
import nextstep.subway.acceptance.step.LineSectionAcceptanceStep;
import nextstep.subway.acceptance.step.PathAcceptanceStep;
import nextstep.subway.acceptance.step.StationAcceptanceStep;
import nextstep.subway.fixture.StationFixture;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.line.dto.request.SaveLineRequest;
import nextstep.subway.line.dto.request.SaveLineSectionRequest;
import nextstep.subway.path.dto.response.PathResponse;
import nextstep.subway.station.dto.response.StationResponse;
import nextstep.subway.support.AcceptanceTest;
import nextstep.subway.support.AssertUtils;
import nextstep.subway.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.UNDEFINED_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 조회 기능")
@AcceptanceTest
public class PathAcceptanceTest {

    @LocalServerPort
    private int port;

    private static final String STATION_ID_KEY = "id";

    private static final String LINE_ID_KEY = "id";

    private Long 교대역;

    private Long 강남역;

    private Long 양재역;

    private Long 남부터미널역;

    private Long 판교역;

    private Long 삼호선;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    /**
     * <pre>
     * 청량리역
     *
     * 교대역    --- *2호선(10)* ---   강남역
     * |                              |
     * *3호선(2)*                      *신분당선(10)*
     * |                              |
     * 남부터미널역  --- *3호선(3)* --- 양재
     * </pre>
     */
    @BeforeEach
    void setUp() {
        if (RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();

        this.교대역 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.교대역).jsonPath().getLong(STATION_ID_KEY);
        this.강남역 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.강남역).jsonPath().getLong(STATION_ID_KEY);
        this.양재역 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.양재역).jsonPath().getLong(STATION_ID_KEY);
        this.남부터미널역 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.남부터미널역).jsonPath().getLong(STATION_ID_KEY);
        this.판교역 = StationAcceptanceStep.지하철역_생성을_요청한다(StationFixture.판교역).jsonPath().getLong(STATION_ID_KEY);

        SaveLineRequest 생성할_이호선 = SaveLineRequest.builder()
                .name("2호선")
                .color("#52c41a")
                .upStationId(교대역)
                .downStationId(강남역)
                .distance(10)
                .build();
        SaveLineRequest 생성할_신분당선 = SaveLineRequest.builder()
                .name("신분당선")
                .color("#f5222d")
                .upStationId(강남역)
                .downStationId(양재역)
                .distance(10)
                .build();
        SaveLineRequest 생성할_삼호선 = SaveLineRequest.builder()
                .name("3호선")
                .color("#fa8c16")
                .upStationId(교대역)
                .downStationId(남부터미널역)
                .distance(2)
                .build();
        LineAcceptanceStep.지하철_노선_생성을_요청한다(생성할_이호선)
                .jsonPath()
                .getLong(LINE_ID_KEY);
        LineAcceptanceStep.지하철_노선_생성을_요청한다(생성할_신분당선)
                .jsonPath()
                .getLong(LINE_ID_KEY);
        this.삼호선 = LineAcceptanceStep.지하철_노선_생성을_요청한다(생성할_삼호선)
                .jsonPath()
                .getLong(LINE_ID_KEY);

        SaveLineSectionRequest 삼호선에_생성할_구간 = SaveLineSectionRequest.builder()
                .upStationId(남부터미널역)
                .downStationId(양재역)
                .distance(3)
                .build();
        LineSectionAcceptanceStep.지하철_구간_생성을_요청한다(삼호선에_생성할_구간, 삼호선);
    }

    /**
     * <pre>
     * When 교대역 - 양재역의 최단 거리 경로를 조회하면
     * Then 교대역 - 남부터미널역 - 양재역의 경로가 조회된다.
     * Then 5m 거리가 조회된다.
     * </pre>
     */
    @DisplayName("최단 거리 경로를 조회한다.")
    @Test
    void getShortestDistance() {
        // when
        PathResponse 최단_거리_경로_조회_응답 = PathAcceptanceStep.최단_거리_경로_조회를_요청한다(교대역, 양재역)
                .as(PathResponse.class);

        // then
        List<Long> 최단_거리_경로에_존재하는_역_아이디_목록 =  최단_거리_경로_조회_응답.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        Integer 최단_거리 = 최단_거리_경로_조회_응답.getDistance();

        assertAll(
                () -> assertThat(최단_거리_경로에_존재하는_역_아이디_목록).containsExactly(교대역, 남부터미널역, 양재역),
                () -> assertThat(최단_거리).isEqualTo(5)
        );
    }

    /**
     * <pre>
     * When 강남역 - 강남역의 최단 거리 경로를 조회하면
     * Then 조회에 실패한다.
     * </pre>
     */
    @DisplayName("출발역과 도착역이 같은 경로를 조회한다.")
    @Test
    void getSameDepartureAndArrivalStations() {
        // when
        ExtractableResponse<Response> 최단_거리_경로_조회_응답 = PathAcceptanceStep.최단_거리_경로_조회를_요청한다(강남역, 강남역);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(최단_거리_경로_조회_응답, HttpStatus.BAD_REQUEST),
                () -> AssertUtils.assertThatErrorMessage(최단_거리_경로_조회_응답, ErrorCode.SAME_DEPARTURE_AND_ARRIVAL_STATIONS)
        );
    }

    /**
     * <pre>
     * When 강남역 - 판교역(연결되어 있지 않음)의 최단 거리 경로를 조회하면
     * Then 조회에 실패한다.
     * </pre>
     */
    @DisplayName("출발역과 도착역이 같은 경로를 조회한다.")
    @Test
    void getUnlinkedDepartureAndArrivalStations() {
        // when
        ExtractableResponse<Response> 최단_거리_경로_조회_응답 = PathAcceptanceStep.최단_거리_경로_조회를_요청한다(강남역, 판교역);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(최단_거리_경로_조회_응답, HttpStatus.BAD_REQUEST),
                () -> AssertUtils.assertThatErrorMessage(최단_거리_경로_조회_응답, ErrorCode.UNLINKED_DEPARTURE_AND_ARRIVAL_STATIONS)
        );
    }

    /**
     * <pre>
     * When 존재하지 않은 역 - 양재역의 최단 거리 경로를 조회하면
     * Then 조회에 실패한다.
     * </pre>
     */
    @DisplayName("등록되어 있지 않은 역이 출발역인 최단 경로를 조회한다.")
    @Test
    void getShortestDistanceWhenNotExistDepartureStation() {
        // when
        ExtractableResponse<Response> 최단_거리_경로_조회_응답 = PathAcceptanceStep.최단_거리_경로_조회를_요청한다(0L, 판교역);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(최단_거리_경로_조회_응답, HttpStatus.BAD_REQUEST),
                () -> AssertUtils.assertThatErrorMessage(최단_거리_경로_조회_응답, ErrorCode.NOT_EXIST_STATION)
        );
    }

    /**
     * <pre>
     * When 남부터미널역 - 존재하지 않은 역의 최단 거리 경로를 조회하면
     * Then 조회에 실패한다.
     * </pre>
     */
    @DisplayName("등록되어 있지 않은 역이 도착역인 최단 경로를 조회한다.")
    @Test
    void getShortestDistanceWhenNotExistArrivalStation() {
        // when
        ExtractableResponse<Response> 최단_거리_경로_조회_응답 = PathAcceptanceStep.최단_거리_경로_조회를_요청한다(남부터미널역, 0L);

        assertAll(
                () -> AssertUtils.assertThatStatusCode(최단_거리_경로_조회_응답, HttpStatus.BAD_REQUEST),
                () -> AssertUtils.assertThatErrorMessage(최단_거리_경로_조회_응답, ErrorCode.NOT_EXIST_STATION)
        );
    }
}
