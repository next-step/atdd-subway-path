package subway.acceptance.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationSteps {

    public static Map<String, Long> 기본_역_생성() {
        Map<String, Long> stationsMap = new HashMap<>();
        List.of("교대역", "강남역", "역삼역", "선릉역", "삼성역", "잠실역", "강변역", "건대역", "성수역", "왕십리역")
                .forEach(StationSteps::역_생성_API);
        var response = 역_목록_조회_API();
        List<Map<String, Object>> jsonResponse = response.jsonPath().getList("$");
        for (Map<String, Object> station : jsonResponse) {
            stationsMap.put((String) station.get("name"), ((Number) station.get("id")).longValue());
        }
        return stationsMap;
    }

    public static ExtractableResponse<Response> 역_생성_API(final String stationName) {
        Map<String, String> param = new HashMap<>();
        param.put("name", stationName);
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 역_목록_조회_API() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 역_제거_API(final String createdLocation) {
        return RestAssured.given().log().all()
                .when().delete(createdLocation)
                .then().log().all()
                .extract();
    }
}
