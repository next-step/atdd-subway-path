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
import subway.exception.dto.ErrorResponse;
import subway.exception.error.SubwayErrorCode;
import subway.utils.LineAssertions;


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

        지하철역_5개_저장요청();
        노선_1개_저장요청();
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[구간등록 성공] 새로운 구간을 등록한다.")
    void 구간등록요청() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(4L)
            .downStationId(5L)
            .distance(20L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_성공_검증(구간등록HTTP응답, HttpStatus.CREATED, 50L, List.of(2L, 4L, 5L));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[구간등록 성공] 새로운 역을 상행역 기준으로 기존 구간 사이에 등록한다.")
    void createIntermediateSectionWithSameUpStation() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            // 노선은 상행(2) 하행(4) 으로 종점이 등록되어있는 상태이며,
            // 새로운 구간 지하철역을 (2-3) 으로 등록하고자 함
            .upStationId(2L)
            .downStationId(3L)
            .distance(7L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_성공_검증(구간등록HTTP응답, HttpStatus.CREATED, 30L, List.of(2L, 3L, 4L));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[구간등록 성공] 새로운 역을 하행역 기준으로 기존 구간 사이에 등록한다.")
    void createIntermediateSectionWithSameDownStation() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            // 노선은 상행(2) 하행(4) 으로 종점이 등록되어있는 상태이며,
            // 새로운 구간 지하철역을 (3-4) 으로 등록하고자 함
            .upStationId(3L)
            .downStationId(4L)
            .distance(20L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_성공_검증(구간등록HTTP응답, HttpStatus.CREATED, 30L, List.of(2L, 3L, 4L));
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 구간을 추가한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 노선의 총 구간 길이를 검증한다.
     * And: 구간의 지하철역들을 검증한다.
     */
    @Test
    @DisplayName("[구간등록 성공] 새로운 역을 상행종점역으로 등록한다.")
    void createFirstSection() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(1L)
            .downStationId(2L)
            .distance(7L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_성공_검증(구간등록HTTP응답, HttpStatus.CREATED, 37L, List.of(1L, 2L, 4L));
    }

    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 역 사이 길이보다 긴 구간을 추가한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[구간등록 실패] 기존 역 사이의 길이보다 큰 새로운 역을 등록한다.")
    void createSectionLongerDistance() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(40L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_실패_검증(구간등록HTTP응답, SubwayErrorCode.CANNOT_CREATE_SECTION);
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 역 사이 길이와 같은 구간을 추가한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[구간등록 실패] 기존 역 사이의 길이와 같은 새로운 역을 등록한다.")
    void createSectionSameDistance() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(30L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_실패_검증(구간등록HTTP응답, SubwayErrorCode.CANNOT_CREATE_SECTION);
    }


    /**
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 1개의 구간이 등록되어 있다.
     * When: 기존 구간과 같은 구간을 등록한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[구간등록 실패] 상하행종점역 둘 다 이미 등록된 역을 등록한다.")
    void createSectionAlreadyRegistry() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(4L)
            .distance(30L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_실패_검증(구간등록HTTP응답, SubwayErrorCode.CANNOT_CREATE_SECTION);
    }


    /**
     * Scenario: 상하행종점역 둘 중 하나도 포함되어 있지 않은 역을 등록한다.
     * Given: 5개의 지하철역이 등록되어 있다.
     * And: 1개의 노선과 2개의 구간이 등록되어 있다.
     * When: 상하행종점역이 기존 구간 어디에도 포함되어 있지 않은 구간을 등록한다.
     * Then: 실패(400 Bad Request) 응답을 받는다.
     * And: '구간을 등록할 수 없습니다.' 오류 메시지를 검증한다.
     */
    @Test
    @DisplayName("[구간등록 실패] 상하행종점역 둘 중 하나도 포함되어 있지 않은 역을 등록한다.")
    void createSectionNoMatchAnyStation() {
        // Given (Fixture)
        // When
        SectionRequest sectionRequest = SectionRequest.builder()
            .upStationId(10L)
            .downStationId(11L)
            .distance(30L)
            .build();
        ExtractableResponse<Response> 구간등록HTTP응답 = 구간등록요청(sectionRequest);

        // Then
        LineAssertions.구간등록_실패_검증(구간등록HTTP응답, SubwayErrorCode.STATION_NOT_FOUND);
    }


    /**
     * Given: 5개의 지하철역을 등록한다.
     * And: 1개의 노선을 등록한다.
     * And: 3개의 구간을 등록한다.
     * When: 노선을 조회한다.
     * Then: 성공(200 OK) 응답을 받는다.
     * And: 지하철역 아이디 리스트의 순서를 검증한다.
     */
    @Test
    @DisplayName("[구간조회 성공] 구간을 조회한다.")
    public void getSections() {
        // Given
        SectionRequest 기존역사이에추가되는구간 = SectionRequest.builder()
            .upStationId(2L)
            .downStationId(3L)
            .distance(7L)
            .build();
        SectionRequest 새로운상행종점구간 = SectionRequest.builder()
            .upStationId(1L)
            .downStationId(2L)
            .distance(10L)
            .build();
        SectionRequest 새로운하행종점구간 = SectionRequest.builder()
            .upStationId(4L)
            .downStationId(5L)
            .distance(10L)
            .build();
        
        assertThat(구간등록요청(기존역사이에추가되는구간).statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(구간등록요청(새로운상행종점구간).statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(구간등록요청(새로운하행종점구간).statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // When
        ExtractableResponse<Response> 구간조회HTTP응답 = 구간조회요청();

        // Then
        LineAssertions.구간등록_성공_검증(구간조회HTTP응답, HttpStatus.OK, 50L, List.of(1L, 2L, 3L, 4L, 5L));
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
        구간등록요청(secondSectionRequest);

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
        구간등록요청(secondSectionRequest);

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
        구간등록요청(secondSectionRequest);

        SectionRequest thirdSectionRequest = SectionRequest.builder()
            .upStationId(3L)
            .downStationId(4L)
            .distance(20L)
            .build();
        구간등록요청(thirdSectionRequest);

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

    private void 지하철역_5개_저장요청() {
        String stationPath = "/stations";
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(강남역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(광교역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(양재역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(논현역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(신논현역).build());
    }

    private void 노선_1개_저장요청() {
        this.등록한노선 = RestAssuredClient.requestPost(linePath,
            LineRequestFactory.create(신분당선)).extract().as(LineResponse.class);
    }

    private ExtractableResponse<Response> 구간등록요청(SectionRequest sectionRequest) {
        return RestAssuredClient.requestPost(linePath + "/" + 등록한노선.getId() + "/sections",
                sectionRequest)
            .extract();
    }

    private ExtractableResponse<Response> 구간조회요청() {
        return RestAssuredClient.requestGet(linePath + "/" + 등록한노선.getId())
            .extract();
    }

}
