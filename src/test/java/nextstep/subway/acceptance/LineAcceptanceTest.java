package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    public static final int BASIC_DISTANCE = 10;
    private Long 역삼역_ID;
    private Long 선릉역_ID;
    private Long 강남역_ID;
    private Long 왕십리역_ID;

    @BeforeEach
    void setUp() {
        databaseCleanUp.execute();
        역삼역_ID = 지하철_역_생성(역삼역);
        선릉역_ID = 지하철_역_생성(선릉역);
        강남역_ID = 지하철_역_생성(강남역);
        왕십리역_ID = 지하철_역_생성(왕십리역);
    }

    @DisplayName("지하철 노선을 생성하면 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.")
    @Test
    void test_지하철노선_생성() {
        //when
        LineResponse linePostResponse = given()
            .body(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines").then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
        List<StationResponse> stationPostResponses = linePostResponse.getStations();

        //then
        LineResponse lineGetResponse = when().get("/lines").then().extract().jsonPath().getList(".", LineResponse.class).get(0);
        List<StationResponse> stationsResponse = lineGetResponse.getStations();
        assertAll(
            () -> assertThat(lineGetResponse).extracting(LineResponse::getName).isEqualTo(linePostResponse.getName()),
            () -> assertThat(lineGetResponse).extracting(LineResponse::getColor).isEqualTo(linePostResponse.getColor()),
            () -> assertThat(stationsResponse).extracting(StationResponse::getId).isEqualTo(stationPostResponses.stream().map(StationResponse::getId).collect(Collectors.toList())),
            () -> assertThat(stationsResponse).extracting(StationResponse::getName).containsExactly("역삼역", "선릉역")
        );
    }

    @DisplayName("2개의 지하철 노선을 생성하고 지하철 노선 목록을 조회하면 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.")
    @Test
    void test_지하철_노선_목록_조회() {
        //given
        지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));
        지하철_노선_생성(getRequestParam_분당선(강남역_ID, 왕십리역_ID, BASIC_DISTANCE));

        //when
        List<LineResponse> lineResponses = when().get("/lines").then().extract().jsonPath().getList(".", LineResponse.class);
        assertAll(
            () -> assertThat(lineResponses).hasSize(2),
            () -> assertThat(lineResponses).extracting(LineResponse::getName).containsExactly(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE).get("name"), getRequestParam_분당선(강남역_ID, 왕십리역_ID, BASIC_DISTANCE).get("name")),
            () -> assertThat(lineResponses).extracting(LineResponse::getColor).containsExactly(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE).get("color"), getRequestParam_분당선(강남역_ID, 왕십리역_ID, BASIC_DISTANCE).get("color"))
        );
    }

    @DisplayName("지하철 노선을 생성하고 생성한 지하철 노선을 조회하면 생성한 지하철 노선의 정보를 응답받을 수 있다.")
    @Test
    void test_지하철_생성_노선_조회() {
        //given
        LineResponse linePostResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));

        //when
        LineResponse lineResponse_신분당선 = 지하철_노선_조회(linePostResponse.getId());
        assertAll(
            () -> assertThat(lineResponse_신분당선.getId()).isEqualTo(1),
            () -> assertThat(lineResponse_신분당선.getName()).isEqualTo(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE).get("name")),
            () -> assertThat(lineResponse_신분당선.getColor()).isEqualTo(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE).get("color"))
        );
    }

    @DisplayName("지하철 노선을 생성하고 생성한 지하철 노선을 수정하면 해당 지하철 노선 정보는 수정된다.")
    @Test
    void test_지하철_노선_수정() {
        //given
        LineResponse linePostResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));
        Map<String, String> putRequest = Map.of(
            "name", "다른분당선",
            "color", "Red"
        );
        //when
        지하철_노선_수정(putRequest, linePostResponse.getId());

        //then
        LineResponse lineResponse_신분당선_수정 = when().get("/lines/" + linePostResponse.getId()).then().extract().jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse_신분당선_수정.getColor()).isEqualTo(putRequest.get("color"));
        assertThat(lineResponse_신분당선_수정.getName()).isEqualTo(putRequest.get("name"));
    }

    @DisplayName("지하철 노선을 생성하고 생성한 지하철 노선을 삭제하면 해당 지하철 노선 정보는 삭제된다.")
    @Test
    void test_지하철_노선_삭제() {
        //given
        LineResponse linePostResponse = 지하철_노선_생성(getRequestParam_신분당선(역삼역_ID, 선릉역_ID, BASIC_DISTANCE));

        //when & then
        when()
            .delete("/lines/" + linePostResponse.getId())
            .then()
            .log().all().statusCode(HttpStatus.SC_NO_CONTENT);
        when().get("/lines/" + linePostResponse.getId()).then().log().all().statusCode(HttpStatus.SC_NOT_FOUND);
    }

}
