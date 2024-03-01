package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;

import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import io.restassured.http.ContentType;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.PathResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseAcceptanceTest {
    public static final Map<String, String> 역삼역 = Map.of("name", "역삼역");
    public static final Map<String, String> 선릉역 = Map.of("name", "선릉역");
    public static final Map<String, String> 강남역 = Map.of("name", "강남역");
    public static final Map<String, String> 왕십리역 = Map.of("name", "왕십리역");

    public static final Map<String, String> 교대역 = Map.of("name", "교대역");
    public static final Map<String, String> 양재역 = Map.of("name", "양재역");
    public static final Map<String, String> 남부터미널역 = Map.of("name", "남부터미널역");

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

    public Long 지하철_노선_생성_ID(Map<String, String> lineRequestParam) {
        return given()
            .body(lineRequestParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines").then().log().all().extract()
            .jsonPath().getObject(".", LineResponse.class).getId();
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
        return given()
            .pathParam("lineId", lineId)
            .when()
            .get("/lines/{lineId}")
            .then().log().all().extract().jsonPath().getObject(".", LineResponse.class);
    }

    public Map<String, String> getRequestParam_신분당선(Long upStationId, Long downStationId, Integer distance) {
        String lineName = "신분당선";
        String lineColor = "빨간색";

        return Map.of(
            "name", lineName,
            "color", lineColor,
            "upStationId", Long.toString(upStationId),
            "downStationId", Long.toString(downStationId),
            "distance", distance.toString()
        );
    }

    public Map<String, String> getRequestParam_분당선(Long upStationId, Long downStationId, Integer distance) {
        String lineName = "분당선";
        String lineColor = "노란색";

        return Map.of(
            "name", lineName,
            "color", lineColor,
            "upStationId", upStationId.toString(),
            "downStationId", downStationId.toString(),
            "distance", distance.toString()
        );
    }

    public Map<String, String> getRequestParam(String lineName, String lineColor, Long upStationId, Long downStationId, Integer distance) {

        return Map.of(
            "name", lineName,
            "color", lineColor,
            "upStationId", upStationId.toString(),
            "downStationId", downStationId.toString(),
            "distance", distance.toString()
        );
    }

    public void 지하철_노선에_지하철_구간_생성_요청(Long lineId, SectionRequest sectionRequest) {
        given().body(sectionRequest)
            .pathParam("lineId", lineId)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .when().post("/lines/{lineId}/sections").then().log().all();
    }

    public PathResponse 지하철_경로_조회(Long 출발역, Long 도착역) {

        return given()
            .queryParam("sourceId", 출발역)
            .queryParam("targetId", 도착역)
            .accept(ContentType.JSON)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().log().all().extract().jsonPath().getObject(".", PathResponse.class);
    }
}
