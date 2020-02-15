package atdd.station.controller;

import atdd.station.AbstractAcceptanceTest;
import atdd.station.domain.Station;
import atdd.station.dto.station.StationDetailResponseDto;
import atdd.station.dto.station.StationListResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.List;
import java.util.Objects;

import static atdd.station.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AbstractAcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(StationAcceptanceTest.class);

    public static final String KANGNAM_STATION_JSON = "{\"name\": \"" + KANGNAM_STATION_NAME + "\"}";
    private static final String STATION_API_BASE_URL = "/stations/";

    private RestWebClientTest restWebClientTest;

    @BeforeEach
    void setUp() {
        this.restWebClientTest = new RestWebClientTest(this.webTestClient);
    }

    @DisplayName("강남역_지하철_등록을_요청이_성공하는지")
    @ParameterizedTest
    @ValueSource(strings = {KANGNAM_STATION_NAME, PANGYO_STATION_NAME})
    void createStationSuccessTest(String stationName) {
        //when
        EntityExchangeResult<Station> expectResponse
                = restWebClientTest.postMethodAcceptance(STATION_API_BASE_URL, getStation(stationName), Station.class);

        //then
        HttpHeaders responseHeaders = expectResponse.getResponseHeaders();
        assertThat(responseHeaders.getLocation()).isNotNull();
        assertThat(expectResponse.getResponseBody().getName()).isEqualTo(stationName);
    }

    @DisplayName("강남역_지하철이_조회가_성공하는지")
    @Test
    void listStationSuccessTest() {
        //given
        getLocationPathToCreateStation(KANGNAM_STATION_NAME);
        getLocationPathToCreateStation(PANGYO_STATION_NAME);

        //when

        EntityExchangeResult<StationListResponseDto> expectResponse
                = restWebClientTest.getMethodAcceptance(STATION_API_BASE_URL, StationListResponseDto.class);

        StationListResponseDto responseBody = expectResponse.getResponseBody();
        List<Station> stations = Objects.requireNonNull(responseBody).getStations();

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(stations.get(0).getName()).isEqualTo(KANGNAM_STATION_NAME);
        assertThat(stations.get(1).getName()).isEqualTo(PANGYO_STATION_NAME);
    }

    @DisplayName("강남역_지하철_역_정보_상세조회_요청이_성공하는지")
    @Test
    void stationDetailSuccessTest() {
        //given
        String location = getLocationPathToCreateStation(KANGNAM_STATION_NAME);

        //when
        EntityExchangeResult<StationDetailResponseDto> expectResponse
                = restWebClientTest.getMethodAcceptance(location, StationDetailResponseDto.class);
        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(expectResponse.getResponseBody().getName()).isEqualTo(KANGNAM_STATION_NAME);
    }


    @DisplayName("강남역_지하철_역_정보_삭제_요청이_성공하는지")
    @Test
    void stationDeleteSuccessTest() {
        String location = getLocationPathToCreateStation(KANGNAM_STATION_NAME);

        //when
        EntityExchangeResult expectResponse = restWebClientTest.deleteMethodAcceptance(location);

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
    }

    private String getLocationPathToCreateStation(String name) {
        return Objects.requireNonNull(
                restWebClientTest.postMethodAcceptance(STATION_API_BASE_URL, getStation(name), Station.class)
                .getResponseHeaders()
                .getLocation())
                .getPath();
    }
}
