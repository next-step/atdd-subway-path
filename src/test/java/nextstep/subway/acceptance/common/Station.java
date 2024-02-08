package nextstep.subway.acceptance.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import nextstep.subway.interfaces.station.dto.StationResponse;

import java.util.List;

public class Station {

    public static class RequestBody {
        public final String name;

        private RequestBody(String name) {
            this.name = name;
        }
    }

    public static class Api {

        public static ExtractableResponse<Response> createStationBy(RequestBody requestBody) {
            return RestAssured.given().log().all().body(requestBody).contentType(MediaType.APPLICATION_JSON_VALUE).when().post("/stations").then().log().all().extract();
        }

        public static ExtractableResponse<Response> listStation() {
            return RestAssured.given().log().all().when().get("/stations").then().log().all().extract();
        }

        public static List<String> listStationName() {
            return listStation().jsonPath().getList("name", String.class);
        }

        public static ExtractableResponse<Response> deleteStationBy(Long id) {
            return RestAssured.given().log().all().when().delete("/stations/{id}", id).then().log().all().extract();
        }
    }
    public static StationResponse 랜덤역생성() {
        final String name = RandomStringUtils.randomAlphanumeric(10);
        return Station.Api.createStationBy(new RequestBody(name)).as(StationResponse.class);
    }

    public static RequestBody 랜덤_REQUEST_BODY() {
        final String name = RandomStringUtils.randomAlphanumeric(10);
        return new RequestBody(name);
    }


}
