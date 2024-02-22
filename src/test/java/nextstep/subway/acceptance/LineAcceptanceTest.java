package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.fixture.StationFixture;
import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.LineUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 노선 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = {"/truncate.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LineAcceptanceTest {

    @BeforeEach
    void setUp() {
        for (String name : List.of("강남역", "역삼역", "선릉역")) {
            StationFixture.지하철역_생성_요청(name);
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 10, 1L, 2L);
        ExtractableResponse<Response> response = requestCreateLine(request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> getLinesResponse = requestGetLines();

        assertThat(getLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getLinesResponse.jsonPath().getList("name")).contains(request.getName());

    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest createLineRequest1 = new LineRequest("신분당선", "bg-red-600", 10, 1L, 2L);
        LineRequest createLineRequest2 = new LineRequest("분당선", "bg-green-600", 10, 1L, 3L);
        requestCreateLine(createLineRequest1);
        requestCreateLine(createLineRequest2);

        // when
        ExtractableResponse<Response> response = requestGetLines();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name"))
            .containsExactly(
                createLineRequest1.getName(),
                createLineRequest2.getName()
            );
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineRequest createLineRequest = new LineRequest("신분당선", "bg-red-600", 10, 1L, 2L);
        ExtractableResponse<Response> createLineResponse = requestCreateLine(createLineRequest);

        // when
        Long createdLineId = createLineResponse.jsonPath().getLong("id");

        ExtractableResponse<Response> response = requestGetLine(createdLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("id")).isEqualTo(createdLineId);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        LineRequest createLineRequest = new LineRequest("신분당선", "bg-blue-600", 10, 1L, 2L);
        ExtractableResponse<Response> createLineResponse = requestCreateLine(createLineRequest);

        // when
        Long createdLineId = createLineResponse.jsonPath().getLong("id");

        LineUpdateRequest request = new LineUpdateRequest("다른분당선", "bg-red-600");
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
                .pathParam("id", createdLineId)
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put("/lines/{id}")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> getLineResponse = requestGetLine(createdLineId);

        assertThat(getLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(getLineResponse.jsonPath().getString("name")).isEqualTo(request.getName());
        assertThat(getLineResponse.jsonPath().getString("color")).isEqualTo(request.getColor());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest createLineRequest = new LineRequest("신분당선", "bg-blue-600", 10, 1L, 2L);
        ExtractableResponse<Response> createLineResponse = requestCreateLine(createLineRequest);

        // when
        Long createdLineId = createLineResponse.jsonPath().getLong("id");

        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
                .pathParam("id", createdLineId)
            .when()
                .delete("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }

    public static ExtractableResponse<Response> requestCreateLine(
        LineRequest request
    ) {
        return RestAssured
            .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestGetLine(
        Long id
    ) {
        return RestAssured
            .given().log().all()
                .pathParam("id", id)
            .when()
                .get("/lines/{id}")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> requestGetLines() {
        return RestAssured
            .when()
                .get("/lines")
            .then().log().all()
            .extract();
    }
}
