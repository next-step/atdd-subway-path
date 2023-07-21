package nextstep.subway.acceptance.path;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import nextstep.subway.acceptance.line.LineTestUtils;
import nextstep.subway.acceptance.station.StationTestUtils;
import nextstep.subway.line.dto.PathResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineTestUtils.*;
import static nextstep.subway.acceptance.station.StationTestUtils.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 경로 탐색")
public class PathAcceptanceTest extends AcceptanceTest {

    /**
     * 교대역 --- *2호선* --- 강남역
     * ㅣ                     ㅣ
     * *3호선*              *신분당선*
     * ㅣ                       ㅣ
     * 남부터미널역 --- *3호선* --- 양재역
     * */
    String 교대역_URL;
    String 강남역_URL;
    String 양재역_URL;
    String 남부터미널역_URL;
    String 이호선_URL;
    String 신분당선_URL;
    String 삼호선_URL;

    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역_URL = 지하철역_생성(교대역_정보);
        강남역_URL = 지하철역_생성(강남역_정보);
        양재역_URL = 지하철역_생성(양재역_정보);
        남부터미널역_URL = 지하철역_생성(남부터미널역_정보);
        이호선_URL= 지하철_노선_생성(이호선_생성_요청, 교대역_URL, 강남역_URL, 10);
        신분당선_URL = 지하철_노선_생성(신분당선_생성_요청, 강남역_URL, 양재역_URL, 2);
        삼호선_URL = 지하철_노선_생성(삼호선_생성_요청, 교대역_URL, 남부터미널역_URL, 2);
        지하철_구간_등록(삼호선_URL, 남부터미널역_URL, 양재역_URL, 3);
    }

    /**
     * Given 2호선, 3호선, 신분당선이 있을 때
     * When 교대역에서 강남역으로 가는 노선을 조회하면
     * Then 교대-남부터미널-양재-강남 순으로 역이 조회되고 거리는 7이어야한다.
     */
    @DisplayName("경로 조회 성공")
    @Test
    void findPath() {

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(ContentType.JSON)
                .queryParam("source", 지하철_아이디_획득(교대역_URL))
                .queryParam("target", 지하철_아이디_획득(강남역_URL))
                .when()
                .get("/paths")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        // then
        PathResponse pathResponse = response.body().as(PathResponse.class);
        assertThat(pathResponse.getStations()).hasSize(3);
        assertThat(pathResponse.getDistance()).isEqualTo(7);
    }
}
