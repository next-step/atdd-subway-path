package subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.factory.SubwayNameFactory.강남역;
import static subway.factory.SubwayNameFactory.광교역;
import static subway.factory.SubwayNameFactory.논현역;
import static subway.factory.SubwayNameFactory.수서역;
import static subway.factory.SubwayNameFactory.수인분당선;
import static subway.factory.SubwayNameFactory.신논현역;
import static subway.factory.SubwayNameFactory.신분당선;
import static subway.factory.SubwayNameFactory.양재역;
import static subway.factory.SubwayNameFactory.우이신설선;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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
import subway.dto.StationRequest;

@DisplayName("노선 관련 기능")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        saveStations(); // 노선 생성에 필요한 지하철역 추가
    }

    private static final String linePath = "/lines";

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        RestAssuredClient.requestPost(linePath, LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> response = RestAssuredClient.requestGet(linePath)
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> lineNames = response.jsonPath().getList("name", String.class);
        assertThat(lineNames.size()).isEqualTo(1);
        assertThat(lineNames).contains(신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        RestAssuredClient.requestPost(linePath, LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value());

        RestAssuredClient.requestPost(linePath, LineRequestFactory.create(우이신설선))
            .statusCode(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> response = RestAssuredClient.requestGet(linePath)
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> lineNames = response.jsonPath().getList("name", String.class);
        assertThat(lineNames.size()).isEqualTo(2);
        assertThat(lineNames).contains(신분당선, 우이신설선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> creationResponse = RestAssuredClient.requestPost(linePath,
                LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value()).extract();

        // when
        long lineId = creationResponse.jsonPath().getLong("id");
        String path = generatePathForId(lineId);
        ExtractableResponse<Response> response = RestAssuredClient.requestGet(path)
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat((String) response.jsonPath().get("name")).isEqualTo(신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> creationResponse = RestAssuredClient.requestPost(linePath,
                LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value()).extract();

        // when
        long lineId = creationResponse.jsonPath().getLong("id");
        String path = generatePathForId(lineId);

        String updatedLineName = 수인분당선;
        String updatedLineColor = "bg-yellow-600";
        ExtractableResponse<Response> response =
            RestAssuredClient.requestPut(path,
                    LineRequestFactory.createNameAndColorUpdateParams(updatedLineName, updatedLineColor))
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        JsonPath responseJsonPath = response.jsonPath();
        assertThat((String) responseJsonPath.get("name")).isEqualTo(updatedLineName);
        assertThat((String) responseJsonPath.get("color")).isEqualTo(updatedLineColor);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> creationResponse = RestAssuredClient.requestPost(linePath,
                LineRequestFactory.create(신분당선))
            .statusCode(HttpStatus.CREATED.value()).extract();

        // when
        long lineId = creationResponse.jsonPath().getLong("id");
        String path = generatePathForId(lineId);
        ExtractableResponse<Response> response = RestAssuredClient.requestDelete(path)
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    private String generatePathForId(long id) {
        return new StringBuilder()
            .append(linePath)
            .append("/")
            .append(id)
            .toString();
    }

    private void saveStations() {
        String stationPath = "/stations";
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(강남역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(광교역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(양재역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(논현역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(신논현역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(수서역).build());
    }
}
