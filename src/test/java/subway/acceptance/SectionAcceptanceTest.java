package subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.factory.SubwayNameFactory.강남역;
import static subway.factory.SubwayNameFactory.광교역;
import static subway.factory.SubwayNameFactory.논현역;
import static subway.factory.SubwayNameFactory.신논현역;
import static subway.factory.SubwayNameFactory.신분당선;
import static subway.factory.SubwayNameFactory.양재역;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import subway.acceptance.factory.LineRequestFactory;
import subway.acceptance.utils.RestAssuredClient;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.dto.ErrorResponse;
import subway.exception.error.SubwayErrorCode;


@DisplayName("구간 관련 기능")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    private final String linePath = "/lines";

    private LineResponse lineResponse;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        saveStations();
        saveLine();
    }

    /**
     * Given: 2개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * When: 구간을 생성한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선 정보를 응답 받는다.
     * And: 새롭게 추가된 마지막 지하철역(하행종점) 아이디를 비교한다.
     */
    @Test
    @DisplayName("새로운 구간을 등록한다.")
    void createSection() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(20L)
            .build();
        ExtractableResponse<Response> response = RestAssuredClient.requestPost(
            generatePathWithLineId(lineResponse.getId()),
            sectionRequest).extract();

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse.getDistance()).isEqualTo(30L);

        List<StationResponse> lineStations = lineResponse.getStations();
        int stationCount = lineStations.size();
        assertThat(stationCount).isEqualTo(3);
        assertThat(lineStations.get(stationCount - 1).getId()).isEqualTo(3L);
    }

    /**
     * Given: 2개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * When: 상행역이 노선의 하행 종점역이 아닌 구간을 등록한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간의 상행역과 노선의 하행 종점역이 일치하지 않습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("상행역이 노선의 하행 종점역이 아닌 구간을 등록한다.")
    void createSectionWithNoMatchesUpStation() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            // 노선은 상행(1) 하행(2) 으로 종점이 등록되어있는 상태이며,
            // 새로운 구간은 상행(3) 으로 등록하고자 함
            .upStationId(3L)
            .downStationId(4L)
            .distance(20L)
            .build();

        // Then
        ExtractableResponse<Response> response = RestAssuredClient.requestPost(
            generatePathWithLineId(lineResponse.getId()),
            sectionRequest).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getMessage())
            .isEqualTo(SubwayErrorCode.NO_MATCH_UP_STATION.getMessage());

    }

    /**
     * Given: 2개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * When: 하행역이 노선에 등록되어있는 구간을 등록한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간의 하행역이 노선에 이미 등록되어있습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("하행역이 노선에 등록되어 있는 구간을 등록한다.")
    void createSectionWithAlreadyExistDownStation() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            // 노선은 상행(1) 하행(2) 으로 종점이 등록되어있는 상태이며,
            // 새로운 구간의 하행(1) 으로 등록하고자 함
            .upStationId(2L)
            .downStationId(1L)
            .distance(20L)
            .build();

        // Then
        ExtractableResponse<Response> response = RestAssuredClient.requestPost(
            generatePathWithLineId(lineResponse.getId()),
            sectionRequest).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getMessage())
            .isEqualTo(SubwayErrorCode.ALREADY_EXIST_DOWN_STATION.getMessage());

    }

    /**
     * Given: 3개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * And: 2개의 구간이 등록되어 있다.
     * When: 구간을 제거한다.
     * Then: 성공(204 NO CONTENT) 응답을 받는다.
     */
    @Test
    @DisplayName("구간을 제거한다.")
    void deleteSection() {
        // Given
        long lastStationId = 3L;

        SectionRequest secondSectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(20L)
            .build();
        createSection(secondSectionRequest);

        // When
        ExtractableResponse<Response> response = RestAssuredClient.requestDelete(
            generatePathWithLineIdAndStationId(lineResponse.getId(), lastStationId))
            .extract();

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given: 3개의 지하철역이 등록되어 있다.
     * And: 1개의 지하철 노선이 등록되어 있다.
     * And: 2개의 구간이 등록되어 있다.
     * When: 첫 번째 구간(상행 종점역)을 삭제한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '마지막 구간만 삭제할 수 있습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("첫 번째 구간을 삭제한다.")
    void deleteFirstSection() {
        // Given
        long firstStationId = lineResponse.getStations().get(0).getId();
        SectionRequest secondSectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(20L)
            .build();
        createSection(secondSectionRequest);

        // Then
        ExtractableResponse<Response> response = RestAssuredClient.requestDelete(
            generatePathWithLineIdAndStationId(lineResponse.getId(), firstStationId)).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getMessage())
            .isEqualTo(SubwayErrorCode.NON_LAST_STATION_DELETE_NOT_ALLOWED.getMessage());
    }

    /**
     * Given: 4개의 지하철역이 등록되어 있다.
     * And: 1개의 지하철 노선이 등록되어 있다.
     * And: 3개의 구간이 등록되어 있다.
     * When: 두 번째 구간(상행 종점역 & 하행 종점역 둘 중 하나라도 속하지 않은 구간)을 삭제한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '마지막 구간만 삭제할 수 있습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("중간 구간을 삭제한다.")
    void deleteMiddleSection() {
        // Given
        long secondSectionUpStationId = 2L;

        SectionRequest secondSectionRequest = SectionRequest.builder()
            .upStationId(secondSectionUpStationId)
            .downStationId(3L)
            .distance(20L)
            .build();
        createSection(secondSectionRequest);

        SectionRequest thirdSectionRequest = SectionRequest.builder()
            .upStationId(3L)
            .downStationId(4L)
            .distance(20L)
            .build();
        createSection(thirdSectionRequest);

        // Then

        ExtractableResponse<Response> response = RestAssuredClient.requestDelete(
                generatePathWithLineIdAndStationId(lineResponse.getId(), secondSectionUpStationId))
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getMessage())
            .isEqualTo(SubwayErrorCode.NON_LAST_STATION_DELETE_NOT_ALLOWED.getMessage());
    }

    /**
     * Given: 2개의 지하철역이 등록되어 있다.
     * And: 1개의 노선이 등록되어 있다.
     * And: 1개의 구간이 등록되어 있다.
     * When: 구간을 삭제한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '단일 구간 노선의 구간은 삭제할 수 없습니다.' 메시지를 응답받는다.
     */
    @Test
    @DisplayName("단일 구간으로 이루어진 노선에서 구간을 삭제한다.")
    void deleteSingleSection() {
        // Given
        long secondStationId = lineResponse.getStations().get(1).getId();

        // Then
        ExtractableResponse<Response> response = RestAssuredClient.requestDelete(
                generatePathWithLineIdAndStationId(lineResponse.getId(), secondStationId))
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getMessage())
            .isEqualTo(SubwayErrorCode.SINGLE_SECTION_DELETE_NOT_ALLOWED.getMessage());
    }


    private String generatePathWithLineId(long id) {
        return new StringBuilder()
            .append(linePath)
            .append("/")
            .append(id)
            .append("/sections")
            .toString();
    }

    private String generatePathWithLineIdAndStationId(long lineId, long stationId) {
        return new StringBuilder()
            .append(linePath)
            .append("/")
            .append(lineId)
            .append("/sections")
            .append("?stationId=")
            .append(stationId)
            .toString();
    }

    private void saveStations() {
        String stationPath = "/stations";
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(강남역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(광교역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(양재역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(논현역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(신논현역).build());
    }

    private void saveLine() {
        this.lineResponse = RestAssuredClient.requestPost("/lines",
            LineRequestFactory.create(신분당선)).extract().as(LineResponse.class);
    }

    private void createSection(SectionRequest sectionRequest) {
        RestAssuredClient.requestPost(
                generatePathWithLineId(lineResponse.getId()), sectionRequest)
            .extract()
            .as(LineResponse.class);
    }
}
