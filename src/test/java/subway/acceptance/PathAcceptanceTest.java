package subway.acceptance;

import static subway.acceptance.steps.PathSteps.*;
import static subway.factory.SubwayNameFactory.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import subway.dto.PathResponse;

@DisplayName("경로 조회 기능")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PathAcceptanceTest {

    @BeforeEach
    void setUp() {
        지하철역_등록();
        노선_등록();
        구간_등록();
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 3개의 노선이 등록되어 있다.
     * And: 6개의 구간이 등록되어 있다.
     * When: 경로를 조회한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 지하철역 목록을 검증한다.
     * And: 경로 거리를 검증한다.
     */
    @Test
    @DisplayName("지하철 경로를 조회한다.")
    void 지하철_경로_조회() {
        // When
        ExtractableResponse<Response> 조회한_경로 = 경로_조회(5L, 1L)
            .statusCode(HttpStatus.OK.value())
            .extract();

        // Then
        조회한_경로_검증(조회한_경로.as(PathResponse.class), 3L, List.of(고속터미널역, 반포역, 논현역));
    }

}
