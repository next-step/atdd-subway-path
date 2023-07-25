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
import subway.dto.SectionResponse;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.dto.ErrorResponse;
import subway.exception.error.SubwayErrorCode;


@DisplayName("구간 관련 기능")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    private final String linePath = "/lines";

    private LineResponse 등록한노선;

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
            .upStationId(4L)
            .downStationId(5L)
            .distance(20L)
            .build();
        ExtractableResponse<Response> response = RestAssuredClient.requestPost(
            generateSectionPathWithLineId(등록한노선.getId()),
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
            generateSectionPathWithLineId(등록한노선.getId()),
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
            generateSectionPathWithLineId(등록한노선.getId()),
            sectionRequest).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getMessage())
            .isEqualTo(SubwayErrorCode.ALREADY_EXIST_DOWN_STATION.getMessage());

    }

    /**
     * Given: 2개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * Then: 노선의 구간을 조회한다.
     * And: 구간 간의 길이를 검증한다.
     * And: 노선의 지하철역 개수를 검증한다.
     * And: 노선 구간의 하행종점역을 검증한다.
     */
    @Test
    @DisplayName("새로운 역을 상행종점역과 하행종점역 사이에 등록한다.")
    void createIntermediateSection() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            // 노선은 상행(2) 하행(4) 으로 종점이 등록되어있는 상태이며,
            // 새로운 구간 지하철역을 (2-3) 으로 등록하고자 함
            .upStationId(2L)
            .downStationId(3L)
            .distance(7L)
            .build();
        ExtractableResponse<Response> 구간추가HTTP응답 = createSection(sectionRequest);

        // Then
        assertThat(구간추가HTTP응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse 구간추가응답 = 구간추가HTTP응답.as(LineResponse.class);
        assertThat(구간추가응답.getDistance()).isEqualTo(10L);

        // And
        ExtractableResponse<Response> 구간조회HTTP응답 = getSections(구간추가응답.getId());

        // Then
        assertThat(구간조회HTTP응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<SectionResponse> 구간조회리스트 = 구간조회HTTP응답.body().jsonPath().getList("", SectionResponse.class);
        assertThat(구간조회리스트.get(0).getUpStationId()).isEqualTo(2L);
        assertThat(구간조회리스트.get(0).getDownStationId()).isEqualTo(3L);
        assertThat(구간조회리스트.get(1).getDownStationId()).isEqualTo(5L);

        assertThat(구간조회리스트.get(0).getDistance()).isEqualTo(7L);
        assertThat(구간조회리스트.get(1).getDistance()).isEqualTo(23L);
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * Then: 노선의 구간을 조회한다.
     * And: 성공(200 OK) 응답을 받는다.
     * And: 구간의 지하철역들을 검증한다.
     * And: 구간 간의 길이를 검증한다.
     * And: 노선 구간의 하행종점역을 검증한다.
     */
    @Test
    @DisplayName("새로운 역을 상행종점역과 하행종점역 사이에 등록한다.")
    void createFirstSection() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(1L)
            .downStationId(2L)
            .distance(7L)
            .build();
        ExtractableResponse<Response> 구간추가HTTP응답 = createSection(sectionRequest);

        // Then
        LineResponse 구간추가응답 = 구간추가HTTP응답.as(LineResponse.class);
        assertThat(구간추가응답.getDistance()).isEqualTo(10L);

        // And
        ExtractableResponse<Response> 구간조회HTTP응답 = getSections(구간추가응답.getId());

        // Then
        assertThat(구간조회HTTP응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<SectionResponse> 구간조회리스트 = 구간조회HTTP응답.body().jsonPath().getList("", SectionResponse.class);
        assertThat(구간조회리스트.get(0).getUpStationId()).isEqualTo(1L);
        assertThat(구간조회리스트.get(0).getDownStationId()).isEqualTo(2L);
        assertThat(구간조회리스트.get(1).getDownStationId()).isEqualTo(4L);

        assertThat(구간조회리스트.get(0).getDistance()).isEqualTo(7L);
        assertThat(구간조회리스트.get(1).getDistance()).isEqualTo(30L);
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 역 사이 길이보다 긴 구간을 추가한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("기존 역 사이의 길이보다 큰 새로운 역을 등록한다.")
    void createSectionLongerDistance() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(40L)
            .build();
        ExtractableResponse<Response> 구간추가HTTP응답 = createSection(sectionRequest);

        // Then
        assertThat(구간추가HTTP응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse 오류응답 = 구간추가HTTP응답.as(ErrorResponse.class);
        assertThat(오류응답.getMessage()).isEqualTo("구간을 등록할 수 없습니다.");
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 역 사이 길이와 같은 구간을 추가한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("기존 역 사이의 길이와 같은 새로운 역을 등록한다.")
    void createSectionSameDistance() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(30L)
            .build();
        ExtractableResponse<Response> 구간추가HTTP응답 = createSection(sectionRequest);

        // Then
        assertThat(구간추가HTTP응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse 오류응답 = 구간추가HTTP응답.as(ErrorResponse.class);
        assertThat(오류응답.getMessage()).isEqualTo("구간을 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("상하행종점역 둘 다 이미 등록된 역을 등록한다.")
    void createSectionAlreadyRegistry() {
// Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(4L)
            .distance(30L)
            .build();
        ExtractableResponse<Response> 구간추가HTTP응답 = createSection(sectionRequest);

        // Then
        assertThat(구간추가HTTP응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse 오류응답 = 구간추가HTTP응답.as(ErrorResponse.class);
        assertThat(오류응답.getMessage()).isEqualTo("구간을 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("상하행종점역 둘 중 하나도 포함되어 있지 않은 역을 등록한다.")
    void createSectionNoMatchAnyStation() {
// Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(10L)
            .downStationId(11L)
            .distance(30L)
            .build();
        ExtractableResponse<Response> 구간추가HTTP응답 = createSection(sectionRequest);

        // Then
        assertThat(구간추가HTTP응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse 오류응답 = 구간추가HTTP응답.as(ErrorResponse.class);
        assertThat(오류응답.getMessage()).isEqualTo("구간을 등록할 수 없습니다.");
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
            generatePathWithLineIdAndStationId(등록한노선.getId(), lastStationId))
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
        long firstStationId = 등록한노선.getStations().get(0).getId();
        SectionRequest secondSectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(20L)
            .build();
        createSection(secondSectionRequest);

        // Then
        ExtractableResponse<Response> response = RestAssuredClient.requestDelete(
            generatePathWithLineIdAndStationId(등록한노선.getId(), firstStationId)).extract();
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
                generatePathWithLineIdAndStationId(등록한노선.getId(), secondSectionUpStationId))
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
        long secondStationId = 등록한노선.getStations().get(1).getId();

        // Then
        ExtractableResponse<Response> response = RestAssuredClient.requestDelete(
                generatePathWithLineIdAndStationId(등록한노선.getId(), secondStationId))
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getMessage())
            .isEqualTo(SubwayErrorCode.SINGLE_SECTION_DELETE_NOT_ALLOWED.getMessage());
    }


    private String generateSectionPathWithLineId(long id) {
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
        this.등록한노선 = RestAssuredClient.requestPost("/lines",
            LineRequestFactory.create(신분당선)).extract().as(LineResponse.class);
    }

    private ExtractableResponse<Response> createSection(SectionRequest sectionRequest) {
        return RestAssuredClient.requestPost(
                generateSectionPathWithLineId(등록한노선.getId()), sectionRequest)
            .extract();
    }

    private ExtractableResponse<Response> getSections(Long lineId) {
        return RestAssuredClient.requestGet(
                generateSectionPathWithLineId(lineId))
            .extract();
    }
}
