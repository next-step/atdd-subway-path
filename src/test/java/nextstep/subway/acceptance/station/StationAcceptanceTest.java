package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StationAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void before() {
        RestAssured.port = port;
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        final String 강남역 = "강남역";

        지하철역_생성_ID(강남역);
        // then
        List<StationResponse> response = 지하철역_목록_조회();
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getName()).isEqualTo(강남역);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        //given
        final String 창동역 = "창동역";
        final String 노원역 = "노원역";

        지하철역_생성(창동역);
        지하철역_생성(노원역);
        //when
        List<StationResponse> stations = 지하철역_목록_조회();
        //then
        assertThat(stations.size()).isEqualTo(2);
        //then
        List<String> stationNames = stations.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(stationNames).containsExactlyInAnyOrder(노원역, 창동역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        //given
        final String 노원역 = "노원역";
        Long id = 지하철역_생성_ID(노원역);
        //when
        지하철역_삭제(id);
        //then
        assertThat(지하철역_목록_조회().size()).isEqualTo(0);
    }

    private long 지하철역_생성_ID(String name) {
        return 지하철역_생성(name).getId();
    }

    private void 지하철역_삭제(Long id) {
        RestAssured.given().log().all()
                .when()
                .delete(String.format("/stations/%d", id))
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    private List<StationResponse> 지하철역_목록_조회() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList(".", StationResponse.class);
    }

    private StationResponse 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getObject(".", StationResponse.class);
    }
}