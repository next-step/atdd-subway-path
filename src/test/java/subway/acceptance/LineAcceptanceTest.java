package subway.acceptance;

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
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import subway.dto.request.LineModifyRequest;
import subway.dto.request.LineRequest;

import java.util.ArrayList;
import java.util.List;
import subway.fixture.LineFixture;
import subway.fixture.StationFixture;

@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        Long id1 = StationFixture.createStation("지하철역").jsonPath().getLong("id");
        Long id2 = StationFixture.createStation("새로운지하철역").jsonPath().getLong("id");
        Long id3 = StationFixture.createStation("또다른지하철역").jsonPath().getLong("id");

        stationIds.add(id1);
        stationIds.add(id2);
        stationIds.add(id3);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLineAndFindList() {
        //when
        LineRequest request = mockRequest_신분당선();
        LineFixture.createLine(request);
        //then
        List<String> names = this.findLines().jsonPath().getList("name", String.class);
        Assertions.assertThat(names).contains(request.getName());
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
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void create2LineAndFindLines() {
        //when
        LineFixture.createLine(mockRequest_신분당선());
        LineFixture.createLine(mockRequest_분당선());

        //then
        List<String> names = this.findLines().jsonPath().getList("name", String.class);
        Assertions.assertThat(names).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void create2LineAndfindLine() {
        //when
        LineRequest request = mockRequest_신분당선();
        Long id = LineFixture.createLine(request).jsonPath().getLong("id");

        //then
        String name = LineFixture.findLine(id).jsonPath().get("name");
        Assertions.assertThat(name).isEqualTo(request.getName());
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void createLineAndModifyLine() {
        //given
        LineRequest request = mockRequest_신분당선();
        Long id = LineFixture.createLine(request).jsonPath().getLong("id");

        //when
        LineModifyRequest modify = LineModifyRequest.builder()
                .name("다른분당선")
                .color("bg-red-600")
                .build();

        LineFixture.modifyLine(id, modify);

        //then
        String name = LineFixture.findLine(id).jsonPath().get("name");
        Assertions.assertThat(name).isEqualTo(modify.getName());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void createLineAndDeleteLine() {
        //when
        LineRequest request = mockRequest_신분당선();
        Long id = LineFixture.createLine(request).jsonPath().getLong("id");

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

    private LineRequest mockRequest_신분당선() {
        String 신분당선 = "신분당선";
        return LineRequest.builder()
                .name(신분당선)
                .color("bg-red-600")
                .upStationId(stationIds.get(0))
                .downStationId(stationIds.get(1))
                .distance(10L)
                .build();
    }

    private LineRequest mockRequest_분당선() {
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
