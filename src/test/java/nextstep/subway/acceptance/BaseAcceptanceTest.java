package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseAcceptanceTest {
    public static final Map<String, String> 역삼역 = Map.of("name", "역삼역");
    public static final Map<String, String> 선릉역 = Map.of("name", "선릉역");
    public static final Map<String, String> 강남역 = Map.of("name", "강남역");
    public static final Map<String, String> 왕십리역 = Map.of("name", "왕십리역");
    @Autowired
    DatabaseCleanUp databaseCleanUp;

    public Long 지하철_역_생성(Map<String, String> param1) {
        StationResponse stationResponse = given().body(param1)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE).log().all()
                                        .when().post("/stations")
                                        .then().log().all().extract()
                                        .jsonPath().getObject(".", StationResponse.class);
        return stationResponse.getId();
    }


    public LineResponse 지하철_노선_생성(Map<String, String> lineRequestParam) {
        return given()
            .body(lineRequestParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines").then().log().all().extract()
            .jsonPath().getObject(".", LineResponse.class);
    }

    public void 지하철_노선_수정(Map<String, String> lineRequestParam, Long lineId) {
        given()
            .body(lineRequestParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + lineId)
            .then()
            .log().all().statusCode(HttpStatus.SC_OK);
    }

    public LineResponse 지하철_노선_조회(Long lineId) {
        return when()
            .get("/lines/" + lineId)
            .then().extract().jsonPath()
            .getObject(".", LineResponse.class);
    }

    public Map<String, String> getRequestParam_신분당선(Long upStationId, Long downStationId) {
        String lineName = "신분당선";
        String lineColor = "bg-red-600";
        Integer distance = 10;

        return Map.of(
            "name", lineName,
            "color", lineColor,
            "upStationId", Long.toString(upStationId),
            "downStationId", Long.toString(downStationId),
            "distance", distance.toString()
        );
    }

    public Map<String, String> getRequestParam_분당선(Long upStationId, Long downStationId) {
        String lineName = "분당선";
        String lineColor = "bg-yellow-600";
        Integer distance = 2;

        return Map.of(
            "name", lineName,
            "color", lineColor,
            "upStationId", upStationId.toString(),
            "downStationId", downStationId.toString(),
            "distance", distance.toString()
        );
    }

}
