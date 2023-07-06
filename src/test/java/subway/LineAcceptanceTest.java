package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import subway.request.LineModifyRequest;
import subway.request.LineRequest;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.annotation.DirtiesContext.MethodMode.AFTER_METHOD;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    static List<Long> stationIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        addStationCache();
    }

    private static void addStationCache() {
        Long id1 = StationAcceptanceTest.createStation("지하철역").jsonPath().getLong("id");
        Long id2 = StationAcceptanceTest.createStation("새로운지하철역").jsonPath().getLong("id");
        Long id3 = StationAcceptanceTest.createStation("또다른지하철역").jsonPath().getLong("id");

        stationIds.add(id1);
        stationIds.add(id2);
        stationIds.add(id3);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DirtiesContext(methodMode = AFTER_METHOD)
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLineAndFindList() {
        //when
        LineRequest request = getLineRequest1();
        this.createLine(request);
        //then
        List<String> names = this.findLines().jsonPath().getList("name", String.class);
        Assertions.assertThat(names).contains(request.getName());
    }

    private static ExtractableResponse<Response> createLine(LineRequest dto) {
        return RestAssured.given().log().all()
                .body(dto)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> findLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.있다
     */
    @DirtiesContext(methodMode = AFTER_METHOD)
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void create2LineAndFindLines() {
        //when
        this.createLine(getLineRequest1());
        this.createLine(getLineRequest2());

        //then
        List<String> names = this.findLines().jsonPath().getList("name", String.class);
        Assertions.assertThat(names).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DirtiesContext(methodMode = AFTER_METHOD)
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void create2LineAndfindLine() {
        //when
        LineRequest request = getLineRequest1();
        Long id = this.createLine(request).jsonPath().getLong("id");

        //then
        String name = this.findLine(id).jsonPath().get("name");
        Assertions.assertThat(name).isEqualTo(request.getName());
    }


    private static ExtractableResponse<Response> findLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DirtiesContext(methodMode = AFTER_METHOD)
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void createLineAndModifyLine() {
        //given
        LineRequest request = getLineRequest1();
        Long id = this.createLine(request).jsonPath().getLong("id");

        //when
        LineModifyRequest modify = LineModifyRequest.builder()
                .name("다른분당선")
                .color("bg-red-600")
                .build();

        this.modifyLine(id, modify);

        //then
        String name = findLine(id).jsonPath().get("name");
        Assertions.assertThat(name).isEqualTo(modify.getName());
    }

    private static ExtractableResponse<Response> modifyLine(Long id, LineModifyRequest request) {
        return RestAssured.given()
                .body(request).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DirtiesContext(methodMode = AFTER_METHOD)
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void createLineAndDeleteLine() {
        //when
        LineRequest request = getLineRequest1();
        Long id = this.createLine(request).jsonPath().getLong("id");

        deleteLine(id);

        //then
        List<String> names = this.findLines().jsonPath().getList("name", String.class);
        Assertions.assertThat(names).isNotIn(request.getName());
    }

    private static ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private LineRequest getLineRequest1() {
        String 신분당선 = "신분당선";
        return LineRequest.builder()
                .name(신분당선)
                .color("bg-red-600")
                .upStationId(stationIds.get(0))
                .downStationId(stationIds.get(1))
                .distance(10L)
                .build();
    }

    private LineRequest getLineRequest2() {
        String 분당선 = "분당선";
        return LineRequest.builder()
                .name(분당선)
                .color("bg-green-600")
                .upStationId(stationIds.get(0))
                .downStationId(stationIds.get(2))
                .distance(10L)
                .build();
    }
}
