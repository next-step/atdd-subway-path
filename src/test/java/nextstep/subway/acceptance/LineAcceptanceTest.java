package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineResponse;
import nextstep.subway.utils.DatabaseCleanup;
import nextstep.subway.utils.LineFactory;
import nextstep.subway.utils.StationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LineAcceptanceTest {
    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        //databaseCleanup.execute();
        StationFactory.createStation("마천역");
        StationFactory.createStation("방화역");
    }

    /**
     * When 지하철 노선 생성하면
     * Then 지하펄 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     * */
    @DisplayName("지하철노선 생성")
    @Test
    void createLineSuccess() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "5호선");
        params.put("color", "purple");
        params.put("upstationId", "1");
        params.put("downstationId", "2");
        params.put("distance", "10");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.jsonPath().getList("name", String.class)).contains("5호선");
    }

    /**
     * Given 지하철 노선 2개 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     * */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void showLinesSuccess() {
        // given
        LineFactory.createLine("5호선", 1L, 2L);
        LineFactory.createLine("6호선", 1L, 2L);

        // when
        ExtractableResponse<Response> response = LineFactory.getLines();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> lines = response.jsonPath().getList("", LineResponse.class);
        assertThat(lines).hasSize(2);
    }

    /**
     * Given 지하철 노선 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     * */
    @DisplayName("지하철노선 조회")
    @Test
    void showLineSuccess() {
        // given
        Long createdLineId = LineFactory.createLine("5호선", 1L, 2L).getId();

        // when
        ExtractableResponse<Response> response = LineFactory.getLine(createdLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse line = response.as(LineResponse.class);
        assertThat(line.getId()).isEqualTo(createdLineId);
        assertThat(line.getName()).isEqualTo("5호선");
        assertThat(line.getStations()).extracting("name").contains("마천역");
    }

    /**
     * Given 지하철 노선 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     * */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLineSuccess() {
        // given
        Long createdLineId = LineFactory.createLine("5호선", 1L, 2L).getId();

        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "6호선");
        params.put("color", "blue");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .pathParam("id", createdLineId)
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().put("/lines/{id}")
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse line = response.as(LineResponse.class);
        assertThat(line.getName()).isEqualTo("6호선");
        assertThat(line.getColor()).isEqualTo("blue");
    }

    /**
     * Given 지하철 노선 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     * */
    @DisplayName("지하철노선 수정")
    @Test
    void deleteLineSuccess() {
        // given
        Long createdLineId = LineFactory.createLine("5호선", 1L, 2L).getId();

        // when
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .pathParam("id", createdLineId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete("/lines/{id}")
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    }
}
