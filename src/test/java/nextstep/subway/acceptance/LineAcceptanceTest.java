package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
    }

    @Nested
    @DisplayName("라인 생성 테스트")
    public class LineCreationTest {
        /**
         * When 지하철 노선을 생성하면
         * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
         */
        @DisplayName("노멀 케이스")
        @Test
        void createLine() {
            // when
            ExtractableResponse<Response> response = 기본_지하철_노선_생성_요청("2호선", "green");

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            ExtractableResponse<Response> listResponse = 지하철_노선_목록_조회_요청();

            assertThat(listResponse.jsonPath().getList("name")).contains("2호선");
        }

        /**
         * When name이 블랭크인 노선 생성 요청
         * Then 오류가 발생한다.
         */
        @DisplayName("name이 blank인 경우 테스트")
        @Test
        void createLine_nameIsBlank() {
            // when
            ExtractableResponse<Response> response = 기본_지하철_노선_생성_요청("", "green");

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        /**
         * When distance가 0인 노선 생성 요청시,
         * Then 오류가 발생한다.
         */
        @DisplayName("distance가 0인 경우 테스트")
        @Test
        void createLine_distanceIsZero() {
            // when
            ExtractableResponse<Response> response = 기본_지하철_노선_생성_요청("이름", "green", 0);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        기본_지하철_노선_생성_요청("2호선", "green");
        기본_지하철_노선_생성_요청("3호선", "orange");

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).contains("2호선", "3호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 기본_지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 기본_지하철_노선_생성_요청("2호선", "green");

        // when
        Map<String, String> params = new HashMap<>();
        params.put("color", "red");
        params.put("name", "신분당선");
        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createResponse.header("location"))
                .then().log().all().extract();

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("color")).isEqualTo("red");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 기본_지하철_노선_생성_요청("2호선", "green");

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete(createResponse.header("location"))
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 기본_지하철_노선_생성_요청(String name, String color) {
        return 기본_지하철_노선_생성_요청(name, color, 10);
    }

    private ExtractableResponse<Response> 기본_지하철_노선_생성_요청(String name, String color, int distance) {
        return 지하철_노선_생성_요청(name, color, 강남역, 양재역, distance);
    }
}
