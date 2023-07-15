package utils;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AcceptanceUtils {
    private AcceptanceUtils() {
    }

    public static void createStationLineSection(Long lineId, Long upStationId, Long downStationId, BigDecimal distance) {
        createStationLineSection(lineId, upStationId, downStationId, distance, HttpStatus.OK);
    }

    public static void createStationLineSection(Long lineId, Long upStationId, Long downStationId, BigDecimal distance, HttpStatus expectedStatus) {
        final Map<String, String> stationLineSectionCreateRequest = new HashMap<>();

        stationLineSectionCreateRequest.put("upStationId", String.valueOf(upStationId));
        stationLineSectionCreateRequest.put("downStationId", String.valueOf(downStationId));
        stationLineSectionCreateRequest.put("distance", distance.toString());

        RestAssured.given().log().all()
                .body(stationLineSectionCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(expectedStatus.value())
                .extract();
    }

    public static void deleteStationLineSection(Long lineId, Long stationId, HttpStatus expectedStatus) {
        RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .statusCode(expectedStatus.value())
                .extract();
    }

    public static Long createStationLine(String name, String color, Long upStationId, Long downStationId, BigDecimal distance) {
        final Map<String, String> stationLineCreateRequest = new HashMap<>();

        stationLineCreateRequest.put("name", name);
        stationLineCreateRequest.put("color", color);
        stationLineCreateRequest.put("upStationId", String.valueOf(upStationId));
        stationLineCreateRequest.put("downStationId", String.valueOf(downStationId));
        stationLineCreateRequest.put("distance", distance.toString());

        return RestAssured.given().log().all()
                .body(stationLineCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getLong("id");
    }

    public static void updateStationLine(Long lineId, String name, String color) {
        final Map<String, String> stationLineUpdateRequest = new HashMap<>();

        stationLineUpdateRequest.put("name", name);
        stationLineUpdateRequest.put("color", color);

        RestAssured.given().log().all()
                .body(stationLineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    public static void deleteStationLine(Long lineId) {
        RestAssured.given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    public static JsonPath getStationLine(Long stationLineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + stationLineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath();
    }

    public static JsonPath getStationLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath();
    }

    public static List<Long> createStations(List<String> names) {
        return names.stream()
                .map(AcceptanceUtils::createStation)
                .collect(Collectors.toList());
    }

    public static Map<String, Long> createStationsAndGetStationMap(List<String> names) {
        return names.stream()
                .collect(Collectors.toMap(Function.identity(), AcceptanceUtils::createStation));
    }

    public static JsonPath getStations() {
        return RestAssured.given().log().all()
                .get("/stations")
                .then().log().all()
                .extract()
                .jsonPath();
    }

    public static void deleteStation(Long stationId) {
        RestAssured.given().log().all()
                .when().delete("stations/" + stationId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    public static Long createStation(String name) {
        final Map<String, String> stationCreateRequest = new HashMap<>();
        stationCreateRequest.put("name", name);

        return RestAssured.given().log().all()
                .body(stationCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/stations")
                .then().log().all()
                .extract()
                .jsonPath().getLong("id");
    }

    public static JsonPath searchStationPath(Long startStationId, Long destinationStationId, HttpStatus status) {
        return RestAssured.given().log().all()
                .queryParam("source", startStationId)
                .queryParam("target", destinationStationId)
                .get("/paths")
                .then().log().all()
                .statusCode(status.value())
                .extract()
                .jsonPath();
    }
}
