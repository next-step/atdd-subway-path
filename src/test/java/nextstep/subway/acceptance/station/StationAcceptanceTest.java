package nextstep.subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.constants.Endpoint;
import nextstep.subway.station.dto.request.SaveStationRequestDto;
import nextstep.subway.utils.AcceptanceTest;
import nextstep.subway.utils.DatabaseCleanup;
import nextstep.subway.utils.RestAssuredClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.UNDEFINED_PORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철역 관련 기능")
@AcceptanceTest
public class StationAcceptanceTest {

    @LocalServerPort
    private int port;

    private static final String STATION_BASE_URL = Endpoint.STATION_BASE_URL.getUrl();

    private static final String STATION_ID_KEY = "id";

    private static final String STATION_NAME_KEY = "name";

    @Autowired private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        SaveStationRequestDto 강남역 = StationFixture.강남역;
        saveStation(강남역);

        // then
        List<String> stationNames = findStationsAll()
                .jsonPath()
                .getList(STATION_NAME_KEY, String.class);

        assertAll(
                () -> assertThat(stationNames.size()).isEqualTo(1),
                () -> assertThat(stationNames).containsAnyOf(강남역.getName())
        );
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void readStations() {
        //given
        SaveStationRequestDto 강남역 = StationFixture.강남역;
        SaveStationRequestDto 광교역 = StationFixture.광교역;

        Stream.of(강남역, 광교역)
                .forEach(this::saveStation);

        // when
        ExtractableResponse<Response> findStationsAllResponse = findStationsAll();
        List<String> stationNames = findStationsAllResponse
                .jsonPath()
                .getList(STATION_NAME_KEY, String.class);

        // then
        assertThat(stationNames)
                .containsOnly(강남역.getName(), 광교역.getName());
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
        SaveStationRequestDto 강남역 = StationFixture.강남역;
        Long savedStationId = saveStation(강남역)
                .jsonPath()
                .getLong(STATION_ID_KEY);

        // when
        String path = String.format("%s/%d", STATION_BASE_URL, savedStationId);
        ExtractableResponse<Response> deleteStationByIdResponse = RestAssuredClient.delete(path);

        // then
        assertAll(
                () -> assertThat(deleteStationByIdResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> {
                    List<String> stationNames = RestAssuredClient.get(STATION_BASE_URL)
                            .jsonPath()
                            .getList(STATION_NAME_KEY, String.class);

                    assertThat(stationNames).doesNotContain(강남역.getName());
                }
        );
    }

    /**
     * <pre>
     * 지하철역을 생성하는 API를 호출하는 함수
     * </pre>
     *
     * @param station
     * @return ExtractableResponse
     */
    private ExtractableResponse<Response> saveStation(SaveStationRequestDto station) {
        ExtractableResponse<Response> saveStationResponse =
                RestAssuredClient.post(STATION_BASE_URL, station);
        assertThat(saveStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return saveStationResponse;
    }

    /**
     * <pre>
     * 모든 지하철역들을 조회하는 API를 호출하는 함수
     * </pre>
     *
     * @return ExtractableResponse
     */
    private ExtractableResponse<Response> findStationsAll() {
        ExtractableResponse<Response> findStationsAllResponse = RestAssuredClient.get(STATION_BASE_URL);
        assertThat(findStationsAllResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        return findStationsAllResponse;
    }

}