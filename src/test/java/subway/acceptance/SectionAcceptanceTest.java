package subway.acceptance;

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
import subway.common.DatabaseCleaner;
import subway.dto.request.LineRequest;
import subway.dto.response.LineResponse;
import subway.dto.response.StationResponse;
import subway.fixture.LineFixture;
import subway.fixture.SectionFixture;
import subway.fixture.StationFixture;

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
        Long 판교역 = createStation("판교역");

        //라인 생성(신논현 - 강남)
        ExtractableResponse<Response> response = LineFixture.createLine(LineRequest.builder()
            .name("신분당선")
            .color("bg-red-600")
            .upStationId(신논현역)
            .downStationId(강남역)
            .distance(10L)
            .build());

        lineId = response.jsonPath().getLong("id");

        //구간 추가(강남 - 양재)
        SectionFixture.createSection(lineId, 강남역, 양재역, 10);

        //구간 추가(양재 - 판교)
        SectionFixture.createSection(lineId, 양재역, 판교역, 50);
    }

    Long createStation(String name) {
        Long id = StationFixture.createStation(name).jsonPath().getLong("id");
        stationMaps.put(name, id);
        return id;
    }


    /**
     * When 기존 구간의 종점을 시작으로 하는 지하철 구간을 생성하면
     * Then 지하철 구간이 생성된다
     * Then 지하철 라인 역 목록 조회 시 추가 한 구간의 종점 역을 찾을 수 있다.
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 종점에 생성")
    @Test
    void createSectionByDownStation() {
        Long 판교역 = stationMaps.get("판교역");
        Long 정자역 = createStation("정자역");

        // when
        ExtractableResponse<Response> response = SectionFixture.createSection(lineId, 판교역, 정자역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        var lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse.getStations().stream().map(StationResponse::getName)).containsAnyOf("정자역");

        assertThat(lineResponse.getTotalDistance()).isEqualTo(80);
    }

    /**
     * When 기존 구간의 시작점을 종점으로 하는 지하철 구간을 생성하면
     * Then 지하철 구간이 생성된다
     * Then 지하철 라인 역 목록 조회 시 추가 한 구간의 역을 찾을 수 있다.
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 시작점에 생성")
    @Test
    void createSectionByUpstation() {
        Long 신논현역 = stationMaps.get("신논현역");
        Long 논현역 = createStation("논현역");

        // when
        ExtractableResponse<Response> response = SectionFixture.createSection(lineId, 논현역, 신논현역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        var lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse.getStations().stream().map(StationResponse::getName)).containsAnyOf("논현역");

        assertThat(lineResponse.getTotalDistance()).isEqualTo(80);
    }

    /**
     * When 중간 거리의 지하철 구간을 생성(upStation 이 같은)
     * Then 지하철 구간이 생성된다
     * Then 지하철 라인 역 목록 조회 시 추가 한 구간의 종점 역을 찾을 수 있다.
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 중간에 생성 - upStation 을 동일하게")
    @Test
    void createSectionMiddleWithSameUpStation() {
        Long 양재역 = stationMaps.get("양재역");
        Long 양재시민의숲역 = createStation("양재시민의숲역");

        // when
        ExtractableResponse<Response> response = SectionFixture.createSection(lineId, 양재역, 양재시민의숲역, 20);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        JsonPath line = response.jsonPath();
        List<String> stations = line.getList("stations.name", String.class);
        assertThat(stations).containsAnyOf("양재시민의숲역");
        assertThat(stations).hasSize(5);

        var totalDistance = line.getLong("totalDistance");
        assertThat(totalDistance).isEqualTo(70);
    }

    /**
     * When 장 거리의 지하철 구간을 생성하고, 중간 거리의 지하철 구간을 생성(downStation 이 같은)
     * Then 지하철 구간이 생성된다
     * Then 지하철 라인 역 목록 조회 시 추가 한 구간의 종점 역을 찾을 수 있다.
     */
    @DisplayName("지하철 구간을 생성한다. 구간의 중간에 생성 - downStation 을 동일하게")
    @Test
    void createSectionMiddleWithSameDownStation() {
        Long 양재시민의숲역 = createStation("양재시민의숲역");
        Long 판교역 = stationMaps.get("판교역");

        // when
        ExtractableResponse<Response> response = SectionFixture.createSection(lineId, 양재시민의숲역, 판교역, 30);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        JsonPath line = response.jsonPath();
        List<String> stations = line.getList("stations.name", String.class);
        assertThat(stations).containsAnyOf("양재시민의숲역");
        assertThat(stations).hasSize(5);

        var totalDistance = line.getLong("totalDistance");
        assertThat(totalDistance).isEqualTo(70);
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
        ExtractableResponse<Response> response = SectionFixture.createSection(lineId, 신사역, 논현역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행 역이 하행 종점역이 아니면서
     * When 상행 역은 포함하고, 하행 역은 포함되어 있지 않는 구간을 생성할때 거리를 기존과 같게 설정한 경우
     * Then 지하철 구간이 생성되지 않는다.
     */
    @DisplayName("지하철 구간 생성 실패 - 등록 된 거리와 같을 경우")
    @Test
    void failToCreateSectionBySameDistance() {
        Long 양재역 = stationMaps.get("양재역");
        Long 양재시민의숲역 = createStation("양재시민의숲역");
        Long 판교역 = stationMaps.get("판교역");

        // when
        ExtractableResponse<Response> 양재역_양재시민의숲역 = SectionFixture.createSection(lineId, 양재역, 양재시민의숲역, 50);

        // then
        assertThat(양재역_양재시민의숲역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // when
        ExtractableResponse<Response> 양재시민의숲역_판교역 = SectionFixture.createSection(lineId, 양재시민의숲역, 판교역, 50);

        // then
        assertThat(양재시민의숲역_판교역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행 역이 하행 종점역이 아니면서
     * When 상행 역은 포함하고, 하행 역은  포함되어 있지 않는 구간을 생성할때 거리를 기존보다 크게 설정한 경우
     * Then 지하철 구간이 생성되지 않는다.
     */
    @DisplayName("지하철 구간 생성 실패 - 등록 된 거리 보다 클 경우")
    @Test
    void failToCreateSectionByLongDistance() {
        Long 양재역 = stationMaps.get("양재역");
        Long 양재시민의숲역 = createStation("양재시민의숲역");
        Long 판교역 = stationMaps.get("판교역");

        // when
        ExtractableResponse<Response> 양재역_양재시민의숲역 = SectionFixture.createSection(lineId, 양재역, 양재시민의숲역, 100);

        // then
        assertThat(양재역_양재시민의숲역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // when
        ExtractableResponse<Response> 양재시민의숲역_판교역 = SectionFixture.createSection(lineId, 양재시민의숲역, 판교역, 100);

        // then
        assertThat(양재시민의숲역_판교역.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * When 등록되어 있는 하행역 정보로 지하철 구간을 생성하면
     * Then 지하철 구간이 생성되지 않는다.
     */
    @DisplayName("지하철 구간 생성 실패 - 새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없다.(상/하행 다 같을 때)")
    @Test
    void failToCreateSectionByExistStationId() {
        Long 강남역 = stationMaps.get("강남역");
        Long 양재역 = stationMaps.get("양재역");

        // when
        ExtractableResponse<Response> response = SectionFixture.createSection(lineId, 강남역, 양재역, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 2개 구간을 가지고 있는 라인을 생성한다.
     * When 마지막 구간에 속한 역아이디로 삭제 요청을 한다.
     * Then 지하철 구간이 삭제된다.
     * Then 1개 구간 -> 2개의 station 만 존재함.
     */
    @DisplayName("지하철 구간을 삭제한다. - 성공")
    @Test
    void deleteSection() {
        //given
        Long 판교역 = stationMaps.get("판교역");

        // when
        ExtractableResponse<Response> response = SectionFixture.deleteSection(lineId, 판교역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(getStations()).hasSize(3);
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
        ExtractableResponse<Response> response = SectionFixture.deleteSection(lineId, 시청역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getStations()).hasSize(4);
    }

    private List<String> getStations() {
        List<String> stations = LineFixture.findLine(lineId).jsonPath().getList("stations");
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
        ExtractableResponse<Response> response = SectionFixture.deleteSection(lineId, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getStations()).hasSize(4);
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
        Long 판교역 = stationMaps.get("판교역");
        // given 구간 한개로
        SectionFixture.deleteSection(lineId, 판교역);
        SectionFixture.deleteSection(lineId, 양재역);

        // then
        ExtractableResponse<Response> response = SectionFixture.deleteSection(lineId, 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(getStations()).hasSize(2);
    }
}
