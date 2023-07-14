package subway;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.common.DatabaseCleaner;
import subway.dto.request.LineRequest;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    Long lineId;

    Map<String, Long> stationMaps = new HashMap<>();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.execute();

        createFixtureData();
    }

    void createFixtureData() {
        //역 생성
        Long 신논현역 = createStation("신논현역");
        Long 강남역 = createStation("강남역");
        Long 양재역 = createStation("양재역");

        //라인 생성(신논현 - 강남)
        ExtractableResponse<Response> response = LineAcceptanceTest.createLine(LineRequest.builder()
            .name("신분당선")
            .color("bg-red-600")
            .upStationId(신논현역)
            .downStationId(강남역)
            .distance(10L)
            .build());

        lineId = response.jsonPath().getLong("id");

        //구간 추가(강남 - 양재)
        this.createSection(강남역, 양재역, 10);
    }

    Long createStation(String name) {
        Long id = StationAcceptanceTest.createStation(name).jsonPath().getLong("id");
        stationMaps.put(name, id);
        return id;
    }


    /**
     * POST /lines/1/sections
     *
     * @return
     */
    ExtractableResponse<Response> createSection(Long upStationId, Long downStationId, Integer distance) {
        Map<String ,String> params = new HashMap<>();
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        return RestAssured.given().log().all()
            .body(params)
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }

    /**
     * When 지하철 구간을 생성하면
     * Then 지하철 구간이 생성된다
     * Then 지하철 라인 역 목록 조회 시 추가 한 구간의 종점 역을 찾을 수 있다.
     */
    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        Long 양재역 = stationMaps.get("양재역");
        Long 양재시민의숲역 = createStation("양재시민의숲역");

        // when
        ExtractableResponse<Response> response = this.createSection(양재역, 양재시민의숲역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stations = LineAcceptanceTest.findLine(lineId).jsonPath()
            .getList("stations.name", String.class);
        assertThat(stations).containsAnyOf("양재시민의숲역");
    }

    /**
     * When 장 거리의 지하철 구간을 생성하고, 중간 거리의 지하철 구간을 생성
     * Then 지하철 구간이 생성된다
     * Then 지하철 라인 역 목록 조회 시 추가 한 구간의 종점 역을 찾을 수 있다.
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 중간에 생성")
    @Test
    void createSectionMiddle() {
        Long 양재역 = stationMaps.get("양재역");
        Long 양재시민의숲역 = createStation("양재시민의숲역");
        Long 판교역 = createStation("판교역");

        // when
        this.createSection(양재역, 판교역, 50);
        ExtractableResponse<Response> response = this.createSection(양재역, 양재시민의숲역, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        JsonPath line = LineAcceptanceTest.findLine(lineId).jsonPath();
        List<String> stations = line.getList("stations.name", String.class);
        assertThat(stations).containsAnyOf("양재시민의숲역", "판교역");
    }

    /**
     * When 하행 역으로 시작하지 않는 지하철 구간을 생성하면
     * Then 지하철 구간이 생성되지 않는다.
     */
    @DisplayName("지하철 구간 생성 실패 - 등록되어 있는 하행 종점역이랑 다를경우(상/하행 모두 포함되어 있지 않음)")
    @Test
    void failToCreateSectionByDownStationId() {
        Long 신사역 = createStation("신사역");
        Long 논현역 = createStation("논현역");

        // when
        ExtractableResponse<Response> response = this.createSection(신사역, 논현역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행 역이 하행 종점역이 아니면서
     * When 상행 역은 포함하고, 하행 역은 포함되어 있지 않는 구간을 생성할때 거리를 기존과 같게 설정한 경우
     * Then 지하철 구간이 생성되지 않는다.
     */
    @DisplayName("지하철 구간 생성 실패 - 상행만 포함되어 있때, 등록 된 거리와 같을 경우)")
    @Test
    void failToCreateSectionBySameDistance() {
        Long 강남역 = stationMaps.get("강남역");
        Long 양재시민의숲역 = createStation("양재시민의숲역");

        // when
        ExtractableResponse<Response> response = this.createSection(강남역, 양재시민의숲역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행 역이 하행 종점역이 아니면서
     * When 상행 역은 포함하고, 하행 역은  포함되어 있지 않는 구간을 생성할때 거리를 기존보다 크게 설정한 경우
     * Then 지하철 구간이 생성되지 않는다.
     */
    @DisplayName("지하철 구간 생성 실패 - 상행만 포함되어 있을때, 등록 된 거리 보다 클 경우)")
    @Test
    void failToCreateSectionByLongDistance() {
        Long 강남역 = stationMaps.get("강남역");
        Long 청계산역 = createStation("청계산역");

        // when
        ExtractableResponse<Response> response = this.createSection(강남역, 청계산역, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * When 하행 역으로 시작하지 않는 지하철 구간을 생성하면
     * Then 지하철 구간이 생성되지 않는다.
     */
    @DisplayName("지하철 구간 생성 실패 - 새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없다.(상/하행 다 같을 때)")
    @Test
    void failToCreateSectionByExistStationId() {
        Long 강남역 = stationMaps.get("강남역");
        Long 양재역 = stationMaps.get("양재역");

        // when
        ExtractableResponse<Response> response = this.createSection(강남역, 양재역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * DELETE /lines/1/sections?stationId=2
     *
     * @return
     */
    ExtractableResponse<Response> deleteSection(Long stationId) {

        return RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .queryParam("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }

    /**
     * Given 2개 구간을 가지고 있는 라인을 생성한다.
     * When 마지막 구간에 속한 역아이디로 삭제 요청을 한다.
     * Then 지하철 구간이 삭제된다.
     * Then 1개 구간 -> 2개의 station 만 존재함.
     */
    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        //given
        Long 양재역 = stationMaps.get("양재역");

        // when
        ExtractableResponse<Response> response = this.deleteSection(양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getStations()).hasSize(2);
    }

    /**
     * Given 라인에 등록되지 않은 역을 생성한다
     * When 라인에 등록되지 않은 역 아이디로 삭제를 요청한다
     * Then 지하철 구간이 삭제되지 않는다.
     */
    @DisplayName("지하철 구간 삭제 실패 - 새로운 구간 제거시 위 조건에 부합하지 않는 경우 에러 처리한다.")
    @Test
    void deleteSectionByNotExistStationId() {
        //given
        Long 시청역 = createStation("시청역");

        // when
        ExtractableResponse<Response> response = this.deleteSection(시청역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getStations()).hasSize(3);
    }

    private List<String> getStations() {
        List<String> stations = LineAcceptanceTest.findLine(lineId).jsonPath().getList("stations");
        return stations;
    }

    /**
     * When 첫번째 구간에 포함된 역 아이디로 삭제를 요청한다
     * Then 지하철 구간이 삭제되지 않는다.
     */
    @DisplayName("지하철 구간 삭제 실패 - 마지막 구간이 아닌 역으로는 삭제할 수 없다.")
    @Test
    void deleteSectionByUpStationId() {
        //given
        Long 강남역 = stationMaps.get("강남역");

        // when
        ExtractableResponse<Response> response = this.deleteSection(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getStations()).hasSize(3);
    }

    /**
     * When 구간이 하나만 있는 하행 역 아이디로 삭제를 요청한다
     * Then 지하철 구간이 삭제되지 않는다.
     */
    @DisplayName("지하철 구간 삭제 실패 - 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
    @Test
    void deleteOnlyOneSection() {
        //given
        Long 강남역 = stationMaps.get("강남역");
        Long 양재역 = stationMaps.get("양재역");
        // given 구간 한개로
        this.deleteSection(양재역);

        // then
        ExtractableResponse<Response> response = this.deleteSection(강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getStations()).hasSize(2);
    }
}
