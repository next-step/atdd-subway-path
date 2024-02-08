package nextstep.subway.acceptance.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import nextstep.subway.interfaces.line.dto.LineResponse;
import nextstep.subway.interfaces.station.dto.StationResponse;

import java.util.HashMap;
import java.util.Map;

public class Line {
    public static class RequestBody {
        public String name;
        public String color;
        public Long upStation;
        public Long downStation;
        public Long distance;

        private RequestBody(String name, String color, Long upStation, Long downStation, Long distance) {
            this.name = name;
            this.color = color;
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }
    }

    public static class Api {
        public static ExtractableResponse<Response> createLineBy(Line.RequestBody line) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", line.name);
            params.put("color", line.color);
            params.put("upStationId", line.upStation);
            params.put("downStationId", line.downStation);
            params.put("distance", line.distance.toString());
            return RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/lines")
                    .then().log().all()
                    .extract();
        }

        public static ExtractableResponse<Response> listLine() {
            return RestAssured.given().log().all()
                    .when().get("/lines")
                    .then().log().all()
                    .extract();
        }

        public static ExtractableResponse<Response> retrieveLineBy(Long id) {
            return RestAssured.given().log().all()
                    .when().get("/lines/{id}", id)
                    .then().log().all()
                    .extract();
        }

        public static ExtractableResponse<Response> updateLineBy(Long id, String newName, String newColor) {
            Map<String, String> params = new HashMap<>();
            params.put("name", newName);
            params.put("color", newColor);

            return RestAssured
                    .given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().patch("/lines/{id}", id)
                    .then().log().all()
                    .extract();
        }

        public static ExtractableResponse<Response> deleteLineBy(Long id) {
            return RestAssured
                    .given().log().all()
                    .when().delete("/lines/{id}", id)
                    .then().log().all()
                    .extract();
        }
    }

    public static Line.RequestBody REQUEST_BODY() {
        final String name = RandomStringUtils.randomAlphanumeric(10);
        final String color = RandomStringUtils.randomAlphanumeric(10);
        final Long distance = 10L;

        StationResponse 상행역 = Station.랜덤역생성();
        StationResponse 하행역 = Station.랜덤역생성();
        return new Line.RequestBody(name, color, 상행역.getId(), 하행역.getId(), distance);
    }
    public static Line.RequestBody REQUEST_BODY(Long upStationId, Long downStationId) {
        final String name = RandomStringUtils.randomAlphanumeric(10);
        final String color = RandomStringUtils.randomAlphanumeric(10);
        final Long distance = 10L;
        return new Line.RequestBody(name, color, upStationId, downStationId, distance);
    }
    public static LineResponse 랜덤노선생성() {
        return Api.createLineBy(REQUEST_BODY()).as(LineResponse.class);
    }
    public static LineResponse 노선생성(Long upStationId, Long downStationId) {
        return Api.createLineBy(REQUEST_BODY(upStationId, downStationId)).as(LineResponse.class);
    }
}
