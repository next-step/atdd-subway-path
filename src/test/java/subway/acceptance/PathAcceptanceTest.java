package subway.acceptance;

import static subway.acceptance.utils.SubwayClient.*;
import static subway.acceptance.assertions.PathAssertions.*;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.exception.error.SubwayErrorCode;

/**
 * 경로 조회 기능 인수 테스트 Fixture
 *
 * 논현역(1L)   --- *신분당선* ---  신논현역(1L)
 * |                            |
 * 반포역(2L)                     |
 * |                            |
 * *7호선*                       *9호선*
 * |                            |
 * 고속터미널역(5L) --- *9호선* --- 사평역(4L)
 *
 * *3호선*
 * ( 구간 연결 X )
 *
 * 압구정역(7L)
 * |
 * 옥수역(6L)
 *
 */

@DisplayName("경로 조회 기능")
public class PathAcceptanceTest extends AcceptanceTest {

    Long 신논현역;
    Long 옥수역;
    Long 논현역;
    Long 반포역;
    Long 사평역;
    Long 고속터미널역;
    Long 압구정역;

    @BeforeEach
    void setUp() {
        신논현역 = 지하철역_생성_요청(new StationRequest("신논현역")).jsonPath().getLong("id");
        옥수역 = 지하철역_생성_요청(new StationRequest("옥수역")).jsonPath().getLong("id");
        압구정역 = 지하철역_생성_요청(new StationRequest("압구정역")).jsonPath().getLong("id");
        논현역 = 지하철역_생성_요청(new StationRequest("논현역")).jsonPath().getLong("id");
        반포역 = 지하철역_생성_요청(new StationRequest("반포역")).jsonPath().getLong("id");
        사평역 = 지하철역_생성_요청(new StationRequest("사평역")).jsonPath().getLong("id");
        고속터미널역 = 지하철역_생성_요청(new StationRequest("고속터미널역")).jsonPath().getLong("id");

        노선_생성_요청(new LineRequest("신분당선", "bg-red-600", 신논현역, 논현역, 2L));
        노선_생성_요청(new LineRequest("삼호선", "bg-orange-600", 압구정역, 옥수역, 2L));
        Long 칠호선 = 노선_생성_요청(new LineRequest("칠호선", "bg-green-600", 논현역, 반포역, 1L)).jsonPath().getLong("id");
        Long 구호선 = 노선_생성_요청(new LineRequest("구호선", "bg-brown-600", 사평역, 신논현역, 5L)).jsonPath().getLong("id");

        구간_생성_요청(칠호선, new SectionRequest(반포역, 고속터미널역, 4L));
        구간_생성_요청(구호선, new SectionRequest(고속터미널역, 사평역, 2L));

    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 3개의 노선이 등록되어 있다.
     * When: 경로를 조회한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 지하철역 목록을 검증한다.
     * And: 경로 거리를 검증한다.
     */
    @Test
    @DisplayName("[성공] 지하철 경로를 조회한다.")
    void 지하철_경로_조회() {
        // When
        ExtractableResponse<Response> 경로_조회_응답 = 경로_조회_요청(고속터미널역, 논현역);

        // Then
        경로_조회_성공_검증(경로_조회_응답, HttpStatus.OK, 5L, List.of("고속터미널역", "반포역", "논현역"));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 3개의 노선이 등록되어 있다.
     * When: 경로를 조회한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '지하철 경로를 조회할 수 없습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("[실패] 출발역과 도착역이 동일한 경로를 조회한다.")
    void 출발역과_도착역이_동일한_경로를_조회() {
        // When
        ExtractableResponse<Response> 경로_조회_응답 = 경로_조회_요청(고속터미널역, 고속터미널역);

        // Then
        경로_조회_실패_검증(경로_조회_응답, HttpStatus.BAD_REQUEST, SubwayErrorCode.CANNOT_FIND_PATH.getMessage());
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 3개의 노선이 등록되어 있다.
     * When: 출발역과 도착역이 연결이 되어 있지 않은 경로를 조회한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '지하철 경로를 조회할 수 없습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("[실패] 출발역과 도착역이 연결이 되어 있지 않은 경로를 조회한다.")
    void 출발역과_도착역이_연결이_되어_있지_않은_경로를_조회한다() {
        // When
        ExtractableResponse<Response> 경로_조회_응답 = 경로_조회_요청(사평역, 압구정역);

        // Then
        경로_조회_실패_검증(경로_조회_응답, HttpStatus.BAD_REQUEST, SubwayErrorCode.CANNOT_FIND_PATH.getMessage());

    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 3개의 노선이 등록되어 있다.
     * When: 존재하지 않는 출발역이나 도착역을 조회한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '지하철역을 찾을 수 없습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("[실패] 존재하지 않는 출발역이나 도착역을 조회한다.")
    void 존재하지_않는_출발역이나_도착역을_조회한다() {
        // When
        ExtractableResponse<Response> 경로_조회_응답 = 경로_조회_요청(0L, 논현역);

        // Then
        경로_조회_실패_검증(경로_조회_응답, HttpStatus.BAD_REQUEST, SubwayErrorCode.STATION_NOT_FOUND.getMessage());

    }
}
