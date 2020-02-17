package atdd.station.controller;

import atdd.station.AbstractAcceptanceTest;
import atdd.station.domain.SubwayLine;
import atdd.station.dto.subwayLine.SubwayLineDetailResponseDto;
import atdd.station.dto.subwayLine.SubwayLineListResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.util.List;
import java.util.Objects;

import static atdd.station.fixture.StationFixture.KANGNAM_AND_YUCKSAM_STATIONS_DTO;
import static atdd.station.fixture.StationFixture.YUCKSAM_STATION_NAME;
import static atdd.station.fixture.SubwayLineFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SubwayLineAcceptanceTest extends AbstractAcceptanceTest {
    static final String SUBWAY_LINE_API_BASE_URL = "/subway-lines/";

    private RestWebClientTest restWebClientTest;

    @BeforeEach
    void setUp() {
        this.restWebClientTest = new RestWebClientTest(this.webTestClient);
    }

    @DisplayName("2호선_지하철노선_생성이_성공하는지")
    @ParameterizedTest
    @ValueSource(strings = {SECOND_SUBWAY_LINE_NAME, FIRST_SUBWAY_LINE_NAME})
    void createSubwayLineSuccessTest(String subwayLineName) {
        //when
        EntityExchangeResult<SubwayLine> expectResponse
                = restWebClientTest.postMethodAcceptance(SUBWAY_LINE_API_BASE_URL, getSubwayLine(subwayLineName), SubwayLine.class);

        //then
        HttpHeaders responseHeaders = expectResponse.getResponseHeaders();
        SubwayLine subwayLine = expectResponse.getResponseBody();

        assertThat(responseHeaders.getLocation()).isNotNull();
        assertThat(subwayLine.getName()).isEqualTo(subwayLineName);
        assertThat(subwayLine.getStartTime()).isEqualTo("05:00");
        assertThat(subwayLine.getEndTime()).isEqualTo("23:50");
        assertThat(subwayLine.getIntervalTime()).isEqualTo("10");
    }

    @DisplayName("지하철노선_조회가_성공하는지")
    @Test
    void listSubwayLineSuccessTest() {
        creatSubwayLine(SECOND_SUBWAY_LINE_NAME);
        creatSubwayLine(FIRST_SUBWAY_LINE_NAME);

        //when
        EntityExchangeResult<SubwayLineListResponseDto> expectResponse
                = restWebClientTest.getMethodAcceptance(SUBWAY_LINE_API_BASE_URL, SubwayLineListResponseDto.class);

        SubwayLineListResponseDto responseBody = expectResponse.getResponseBody();
        List<SubwayLine> subwayLines = Objects.requireNonNull(responseBody).getSubwayLines();

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(subwayLines.size()).isEqualTo(2);
    }

    @DisplayName("지하철노선_상세조회가_성공하는지")
    @Test
    void detailSubwayLineSuccessTest() {
        //given
        String location = creatSubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        EntityExchangeResult<SubwayLineDetailResponseDto> expectResponse
                = restWebClientTest.getMethodAcceptance(location, SubwayLineDetailResponseDto.class);

        SubwayLineDetailResponseDto subwayLine = expectResponse.getResponseBody();

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(expectResponse.getResponseBody().getName()).isEqualTo(SECOND_SUBWAY_LINE_NAME);
        assertThat(subwayLine.getStartTime()).isEqualTo("05:00");
        assertThat(subwayLine.getEndTime()).isEqualTo("23:50");
        assertThat(subwayLine.getIntervalTime()).isEqualTo("10");
    }

    @DisplayName("지하철노선_삭제가_성공하는지")
    @Test
    void deleteSubwayLineSuccessTest() {
        String location = creatSubwayLine(SECOND_SUBWAY_LINE_NAME);

        //when
        EntityExchangeResult expectResponse = restWebClientTest.deleteMethodAcceptance(location);

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("2호선_지하철노선에_강남역과_역삼역을_추가가_성공하는지")
    @Test
    void updateSecondSubwayToAddKanNamStationSuccessTest() {
        String location = creatSubwayLine(SECOND_SUBWAY_LINE_NAME);

        //whens
        EntityExchangeResult<SubwayLine> expectResponse
                = restWebClientTest.postMethodAcceptance(location + "/subways", KANGNAM_AND_YUCKSAM_STATIONS_DTO, SubwayLine.class);

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(expectResponse.getResponseBody().getName()).isEqualTo(SECOND_SUBWAY_LINE_NAME);
    }

    @DisplayName("2호선_지하철노선에_내에_존재하는_강남역을_삭제가_성공하는지")
    @Test
    void deleteKanNamStationInSecondSubwaySuccessTest() {
        String location = restWebClientTest.creatSubwayLine(getSecondSubwayLine());

        //whens
        EntityExchangeResult expectResponse = restWebClientTest.deleteMethodAcceptance(location + "/stations/" + YUCKSAM_STATION_NAME);

        //then
        assertThat(expectResponse.getStatus()).isEqualTo(HttpStatus.OK);
    }


    private String creatSubwayLine(String subwayLineName) {
        return Objects.requireNonNull(restWebClientTest
                .postMethodAcceptance(SUBWAY_LINE_API_BASE_URL, getSubwayLine(subwayLineName), SubwayLine.class)
                .getResponseHeaders()
                .getLocation())
                .getPath();
    }

    public String creatSecondSubwayLine() {
        return Objects.requireNonNull(restWebClientTest
                .postMethodAcceptance(SUBWAY_LINE_API_BASE_URL, getSecondSubwayLine(), SubwayLine.class)
                .getResponseHeaders()
                .getLocation())
                .getPath();
    }
}
