package nextstep.subway.acceptance;

import static nextstep.subway.step.LineSteps.지하철_노선_목록_조회_요청;
import static nextstep.subway.step.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.step.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.step.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.CreateLineRequest;
import nextstep.subway.applicaion.dto.UpdateLineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        // when
        Long a역_id = 지하철역_생성_요청("A역").jsonPath().getLong("id");
        Long b역_id = 지하철역_생성_요청("B역").jsonPath().getLong("id");

        CreateLineRequest request = new CreateLineRequest("2호선", "green", a역_id, b역_id, 5);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 지하철 노선 생성을 요청 하고
     * When 지하철 노선 목록 조회를 요청 하면
     * Then 두 노선이 포함된 지하철 노선 목록을 응답받는다
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        Long a역_id = 지하철역_생성_요청("A역").jsonPath().getLong("id");
        Long b역_id = 지하철역_생성_요청("B역").jsonPath().getLong("id");

        CreateLineRequest 이호선 = new CreateLineRequest("2호선", "green", a역_id, b역_id, 5);
        CreateLineRequest 삼호선 = new CreateLineRequest("3호선", "orange", a역_id, b역_id, 5);

        지하철_노선_생성_요청(이호선);
        지하철_노선_생성_요청(삼호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).contains("2호선", "3호선");
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 조회를 요청 하면
     * Then 생성한 지하철 노선을 응답받는다
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        Long a역_id = 지하철역_생성_요청("A역").jsonPath().getLong("id");
        Long b역_id = 지하철역_생성_요청("B역").jsonPath().getLong("id");

        CreateLineRequest request = new CreateLineRequest("2호선", "green", a역_id, b역_id, 5);
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 지하철 노선의 정보 수정을 요청 하면
     * Then 지하철 노선의 정보 수정은 성공한다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void 지하철_노선_수정() {
        // given
        Long a역_id = 지하철역_생성_요청("A역").jsonPath().getLong("id");
        Long b역_id = 지하철역_생성_요청("B역").jsonPath().getLong("id");

        CreateLineRequest request = new CreateLineRequest("2호선", "green", a역_id, b역_id, 5);
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(request);

        // when
        UpdateLineRequest params = new UpdateLineRequest("새로운 호선", "새로운 색");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(createResponse.header("location"))
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 삭제를 요청 하면
     * Then 생성한 지하철 노선 삭제가 성공한다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void 지하철_노선_삭제() {
        // given
        Long a역_id = 지하철역_생성_요청("A역").jsonPath().getLong("id");
        Long b역_id = 지하철역_생성_요청("B역").jsonPath().getLong("id");

        CreateLineRequest request = new CreateLineRequest("2호선", "green", a역_id, b역_id, 5);
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete(createResponse.header("location"))
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 같은 이름으로 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 실패한다.
     */
    @DisplayName("중복이름으로 지하철 노선 생성")
    @Test
    void 중복이름으로_지하철_노선_생성시_실패() {
        // given
        Long a역_id = 지하철역_생성_요청("A역").jsonPath().getLong("id");
        Long b역_id = 지하철역_생성_요청("B역").jsonPath().getLong("id");

        CreateLineRequest request = new CreateLineRequest("2호선", "green", a역_id, b역_id, 5);
        지하철_노선_생성_요청(request);

        // when
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(request);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
