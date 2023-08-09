package subway.acceptance;

import static subway.acceptance.steps.PathSteps.*;
import static subway.factory.SubwayNameFactory.*;

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
import subway.dto.PathResponse;
import subway.exception.dto.ErrorResponse;
import subway.exception.error.SubwayErrorCode;

@DisplayName("경로 조회 기능")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PathAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        지하철역_등록();
        노선_등록();
        구간_등록();
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
        ExtractableResponse<Response> 조회한_경로 = 경로_조회(5L, 1L)
            .statusCode(HttpStatus.OK.value())
            .extract();

        // Then
        조회한_경로_검증(조회한_경로.as(PathResponse.class), 5L, List.of(고속터미널역, 반포역, 논현역));
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
        ExtractableResponse<Response> 조회한_경로 = 경로_조회(5L, 5L)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract();

        // Then
        경로_조회_예외_검증(조회한_경로.as(ErrorResponse.class),
            SubwayErrorCode.CANNOT_FIND_PATH.getMessage());
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
        ExtractableResponse<Response> 조회한_경로 = 경로_조회(4L, 7L) // 사평 - 압구정
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract();

        // Then
        경로_조회_예외_검증(조회한_경로.as(ErrorResponse.class),
            SubwayErrorCode.CANNOT_FIND_PATH.getMessage());
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
        ExtractableResponse<Response> 조회한_경로 = 경로_조회(0L, 1L)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract();

        // Then
        경로_조회_예외_검증(조회한_경로.as(ErrorResponse.class),
            SubwayErrorCode.STATION_NOT_FOUND.getMessage());
    }
}
