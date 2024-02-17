package nextstep.subway.acceptance.section;


import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.annotation.AcceptanceTest;
import nextstep.subway.acceptance.line.LineApiRequester;
import nextstep.subway.acceptance.station.StationApiRequester;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.SectionCreateRequest;
import nextstep.subway.utils.JsonPathUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철노선 구간 경로 조회 관련 기능")
@AcceptanceTest
public class SectionPathAcceptanceTest {

    Long 교대역id;
    Long 강남역id;
    Long 양재역id;
    Long 남부터미널역id;
    Long 이호선id;
    Long 신분당선id;
    Long 삼호선id;

    @BeforeEach
    void setUp() {
        교대역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("교대역"));
        강남역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("강남역"));
        양재역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("양재역"));
        남부터미널역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("남부터미널역"));

        LineCreateRequest 이호선 = new LineCreateRequest("2호선", "green", 교대역id, 강남역id, 10);
        이호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(이호선));

        LineCreateRequest 신분당선 = new LineCreateRequest("신분당선", "red", 강남역id, 양재역id, 10);
        신분당선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(신분당선));

        LineCreateRequest 삼호선 = new LineCreateRequest("3호선", "orange", 교대역id, 남부터미널역id, 2);
        삼호선id = JsonPathUtil.getId(LineApiRequester.createLineApiCall(삼호선));

        SectionCreateRequest 남부터미널양재역 = new SectionCreateRequest(남부터미널역id, 양재역id, 3);
        SectionApiRequester.generateSection(남부터미널양재역, 삼호선id);
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 경로를 조회하면
     * Then 출발역과 도착역까지의 경로에 있는 역과 거리를 조회한다
     */
    @DisplayName("출발역과 도착역까지의 경로 조회")
    @Test
    void showSectionPaths() {
        //when
        ExtractableResponse<Response> response = given().log().all()
                .queryParam("source", 교대역id)
                .queryParam("target", 양재역id)
                .when().delete("/paths")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        List<Long> ids = response.jsonPath().getList("id", Long.class);
        int distance = response.jsonPath().getInt("distance");
        assertThat(ids).containsExactly(교대역id, 남부터미널역id, 양재역id);
        assertThat(distance).isEqualTo(5);
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 출발역과 도착역을 같게하여 경로를 조회하면
     * Then 예외가 발생한다
     */
    @DisplayName("출발역과 도착역을 같게하여 경로를 조회")
    @Test
    void showSameAsSourceStationAndTargetStation() {
        //when
        ExtractableResponse<Response> response = given().log().all()
                .queryParam("source", 교대역id)
                .queryParam("target", 교대역id)
                .when().delete("/paths")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("출발역과 도착역이 같습니다.");
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 연결되어있지 않은 출발역과 도착역의 경로를 조회하면
     * Then 예외가 발생한다
     */
    @DisplayName("연결되어있지 않은 출발역과 도착역의 경로를 조회")
    @Test
    void showDisConnectedStation() {
        //when
        ExtractableResponse<Response> response = given().log().all()
                .queryParam("source", 교대역id)
                .queryParam("target", 강남역id)
                .when().delete("/paths")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("연결되어있지 않은 출발역과 도착역의 경로는 조회할 수 없습니다.");
    }

    /**
     * Given 지하철 구간을 등록하고
     * When 노선에 등록되어있지 않은 출발역과 도착역의 경로를 조회하면
     * Then 예외가 발생한다
     */
    @DisplayName("존재하지 않는 출발역과 도착역의 경로를 조회")
    @Test
    void showNotExistsStation() {
        Long 성수역id = JsonPathUtil.getId(StationApiRequester.createStationApiCall("성수역"));
        //when
        ExtractableResponse<Response> response = given().log().all()
                .queryParam("source", 성수역id)
                .queryParam("target", 강남역id)
                .when().delete("/paths")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asPrettyString()).isEqualTo("노선에 등록되어있지 않은 출발역과 도착역의 경로는 조회할 수 없습니다.");
    }
}
