package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import nextstep.subway.line.SectionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.acceptance.fixture.StationFixture.loadStations;
import static nextstep.subway.acceptance.fixture.StationFixture.newStation;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        sectionRepository.deleteAllInBatch();
        stationRepository.deleteAllInBatch();
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
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/stations")
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = loadStations().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        // given
        newStation("강남역");
        newStation("역삼역");

        // when
        ExtractableResponse<Response> response = loadStations();

        // then
        List<StationResponse> responses = response.jsonPath().getList(".", StationResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responses).hasSize(2);
        assertThat(responses.stream().map(StationResponse::getName)).containsExactly("강남역", "역삼역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createdResponse = newStation("강남역");
        Long stationId = createdResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse =
                RestAssured.given().log().all()
                        .when().delete("/stations/{id}", stationId)
                        .then().log().all()
                        .extract();

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<String> stationNames = loadStations().jsonPath().getList("name", String.class);
        assertThat(stationNames).doesNotContain("강남역");
    }
}