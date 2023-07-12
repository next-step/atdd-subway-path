package nextstep.line;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.SchemaInitSql;
import nextstep.subway.StationFixture;
import nextstep.subway.SubwayApplication;
import nextstep.subway.line.view.LineCreateRequest;
import nextstep.subway.line.view.LineModifyRequest;
import nextstep.subway.line.view.LineResponse;
import nextstep.subway.station.view.StationResponse;

@SchemaInitSql
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(classes = SubwayApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {
    private static final String API_CREATE_LINE = "/lines";
    private static final String API_GET_LINE = "/lines";
    private static final String API_GET_LINE_LIST = "/lines";
    private static final String API_MODIFY_LINE = "/lines";
    private static final String API_DELETE_LINE = "/lines";
    private final StationFixture stationFixture = new StationFixture();
    private final LineFixture lineFixture = new LineFixture();

    @DisplayName("노선을 생성한다")
    @Test
    void createLine() {
        StationResponse stationA = stationFixture.지하철역_생성("A역");
        StationResponse stationB = stationFixture.지하철역_생성("B역");

        LineResponse lineResponse = lineFixture.노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(),10);

        // then
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toList())).containsSequence(List.of(stationA.getId(), stationB.getId()));
    }

    private ExtractableResponse<Response> 노선생성(String name, String color, long upStationId, long downStationId, int distance) {
        LineCreateRequest request = new LineCreateRequest(name, color, upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                          .body(request)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post(API_CREATE_LINE)
                          .then().log().all()
                          .extract();
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLine() {
        // given
        StationResponse stationA = stationFixture.지하철역_생성("A역");
        StationResponse stationB = stationFixture.지하철역_생성("B역");
        LineResponse createResponse = lineFixture.노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(), 10);

        // when
        ExtractableResponse<Response> response = 노선조회(createResponse.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-red-600");
        assertThat(response.jsonPath().getList("stations.id")).containsSequence(List.of(1, 2));
    }

    @DisplayName("지하철 노선을 조회한다")
    @Test
    void getLineWithValidStationOrder() {
        // given
        StationResponse stationA = stationFixture.지하철역_생성("A역");
        StationResponse stationB = stationFixture.지하철역_생성("B역");
        LineResponse createResponse = lineFixture.노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(), 10);

        // when
        ExtractableResponse<Response> response = 노선조회(createResponse.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-red-600");
        assertThat(response.jsonPath().getList("stations.id")).containsSequence(List.of(1, 2));
    }

    @DisplayName("존재하지 않는 노선을 조회하면 404 처리된다")
    @Test
    void getNotExistLine() {
        // given
        Long notExistLineId = Long.MAX_VALUE;

        // when
        ExtractableResponse<Response> response = 노선조회(notExistLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    private ExtractableResponse<Response> 노선조회(Long createdLineId) {
        return RestAssured.given().log().all()
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().get(getLineRequestUrl(createdLineId))
                          .then().log().all()
                          .extract();
    }

    private String getLineRequestUrl(long id) {
        return API_GET_LINE + "/" + id;
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void getLineList() {
        // given
        StationResponse stationA = stationFixture.지하철역_생성("A역");
        StationResponse stationB = stationFixture.지하철역_생성("B역");
        StationResponse stationC = stationFixture.지하철역_생성("C역");

        노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(), 10);
        노선생성("분당선", "bg-green-600", stationA.getId(), stationC.getId(), 20);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                            .when().get(API_GET_LINE_LIST)
                                                            .then().log().all()
                                                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("name")).containsAnyOf("신분당선", "분당선");
    }

    @DisplayName("지하철 노선을 수정한다")
    @Test
    void modifyLine() {
        // given
        // 지하철 노선을 생성
        StationResponse stationA = stationFixture.지하철역_생성("A역");
        StationResponse stationB = stationFixture.지하철역_생성("B역");

        LineResponse lineResponse = lineFixture.노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(), 10);

        LineModifyRequest lineModifyRequest = new LineModifyRequest("신분당선_수정", "bg-red-300");

        // when
        // 지하철 노선을 수정
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                                                            .body(lineModifyRequest).contentType(MediaType.APPLICATION_JSON_VALUE)
                                                            .when().put(getModifyLineRequestUrl(lineResponse.getId()))
                                                            .then().log().all()
                                                            .extract();

        // then
        // 해당 지하철 노선 정보는 수정된다
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> getLineResponse = 노선조회(lineResponse.getId());

        assertThat(getLineResponse.jsonPath().getString("name")).isEqualTo("신분당선_수정");
        assertThat(getLineResponse.jsonPath().getString("color")).isEqualTo("bg-red-300");

    }

    private String getModifyLineRequestUrl(long id) {
        return API_MODIFY_LINE + "/" + id;
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        // 지하철 노선을 생성
        StationResponse stationA = stationFixture.지하철역_생성("A역");
        StationResponse stationB = stationFixture.지하철역_생성("B역");

        LineResponse lineResponse = lineFixture.노선생성("신분당선", "bg-red-600", stationA.getId(), stationB.getId(), 10);

        // when
        // 노선을 삭제
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                   .when().delete(getDeleteLineRequestUrl(lineResponse.getId()))
                   .then().log().all().extract();

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> getLineResponse = 노선조회(lineResponse.getId());
        assertThat(getLineResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private String getDeleteLineRequestUrl(long id) {
        return API_DELETE_LINE + "/" + id;
    }
}
