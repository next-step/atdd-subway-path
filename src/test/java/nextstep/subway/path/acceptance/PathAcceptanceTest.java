package nextstep.subway.path.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.commons.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionTestRequest;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.station.exception.StationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nextstep.subway.commons.AssertionsUtils.예외메세지_검증;
import static nextstep.subway.line.acceptance.LineUtils.getLineId;
import static nextstep.subway.line.acceptance.LineUtils.지하철노선_생성_요청;
import static nextstep.subway.line.acceptance.SectionUtils.지하철노선_구간생성_요청;
import static nextstep.subway.path.acceptance.PathUtils.*;
import static nextstep.subway.station.acceptance.StationUtils.getStationId;
import static nextstep.subway.station.acceptance.StationUtils.지하철역_생성요청;

public class PathAcceptanceTest extends AcceptanceTest {

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 부천역;
    private Long 역곡역;

    /**
     * 교대역   --- *2호선(길이:10)* ---    강남역
     * |                                    |
     * *3호선(길이:2)*                *신분당선(길이:10)*
     * |                                    |
     * 남부터미널역  --- *3호선(길이:3)* --- 양재
     *
     * 부천역 --- *1호선(길이:5)* --- 역곡역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = getStationId(지하철역_생성요청("교대역"));
        강남역 = getStationId(지하철역_생성요청("강남역"));
        양재역 = getStationId(지하철역_생성요청("양재역"));
        남부터미널역 = getStationId(지하철역_생성요청("남부터미널역"));
        부천역 = getStationId(지하철역_생성요청("부천역"));
        역곡역 = getStationId(지하철역_생성요청("역곡역"));

        LineRequest 이호선_요청 = LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(교대역)
                .downStationId(강남역)
                .distance(10)
                .build();
        지하철노선_생성_요청(이호선_요청);

        LineRequest 신분당선_요청 = LineRequest.builder()
                .name("신분당선")
                .color("red")
                .upStationId(강남역)
                .downStationId(양재역)
                .distance(10)
                .build();
        지하철노선_생성_요청(신분당선_요청);

        LineRequest 삼호선_요청 = LineRequest.builder()
                .name("3호선")
                .color("orange")
                .upStationId(교대역)
                .downStationId(남부터미널역)
                .distance(2)
                .build();
        Long 삼호선 = getLineId(지하철노선_생성_요청(삼호선_요청));

        SectionTestRequest 삼호선_구간생성_요청 = SectionTestRequest.builder()
                .lineId(삼호선)
                .upStationId(남부터미널역)
                .downStationId(양재역)
                .distance(3)
                .build();
        지하철노선_구간생성_요청(삼호선_구간생성_요청);

        LineRequest 일호선_요청 = LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(부천역)
                .downStationId(역곡역)
                .distance(5)
                .build();
        지하철노선_생성_요청(일호선_요청);
    }

    @Test
    void 출발역부터_도착역까지_최단경로_조회() {
        // when
        ExtractableResponse<Response> 지하철_최단경로_조회_응답 = 지하철_최단경로_조회_요청(교대역, 양재역);

        // then
        지하철_최단경로_조회_성공(지하철_최단경로_조회_응답);
        지하철_최단경로_검증(지하철_최단경로_조회_응답, 교대역, 남부터미널역, 양재역);
    }

    @Test
    void 출발역_도착역_같은경우() {
        // when
        ExtractableResponse<Response> 지하철_최단경로_조회_응답 = 지하철_최단경로_조회_요청(부천역, 부천역);

        // then
        지하철_최단경로_조회_실패(지하철_최단경로_조회_응답);
        예외메세지_검증(지하철_최단경로_조회_응답, PathFinder.SAME_STATION_MESSAGE);
    }

    @Test
    void 등록되지_않은_역을_조회하는_경우() {
        // given
        Long 구로역 = 7L;

        // when
        ExtractableResponse<Response> 지하철_최단경로_조회_응답 = 지하철_최단경로_조회_요청(구로역, 양재역);

        // then
        지하철_최단경로_조회_실패(지하철_최단경로_조회_응답);
        예외메세지_검증(지하철_최단경로_조회_응답, StationNotFoundException.MESSAGE + 구로역);
    }

    @Test
    void 출발역_도착역이_연결되지않은_경우() {
        // when
        ExtractableResponse<Response> 지하철_최단경로_조회_응답 = 지하철_최단경로_조회_요청(부천역, 양재역);

        // then
        지하철_최단경로_조회_실패(지하철_최단경로_조회_응답);
        예외메세지_검증(지하철_최단경로_조회_응답, PathFinder.NON_EXIST_PATH_MESSAGE);
    }

}
