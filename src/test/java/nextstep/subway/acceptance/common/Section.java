package nextstep.subway.acceptance.common;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import nextstep.subway.interfaces.station.dto.StationResponse;

public class Section {


    public static class RequestBody {
        public Long upStationId;
        public Long downStationId;
        public Long distance;

        private RequestBody(Long upStationId, Long downStationId, Long distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
    }

    public static class Api {
        public static ExtractableResponse<Response> createBy(Long lineId, Section.RequestBody requestBody) {
            return RestAssured
                    .given()
                    .log().all()
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines/{lineId}/sections", lineId)
                    .then()
                    .log().all()
                    .extract();
        }

        public static ExtractableResponse<Response> deleteBy(Long lineId, Long downStationId) {
            return RestAssured
                    .given()
                    .log().all()
                    .when()
                    .request()
                    .param("stationId", downStationId)
                    .delete("/lines/{id}/sections", lineId)
                    .then()
                    .log().all()
                    .extract();
        }
    }

    public static Section.RequestBody REQUEST_BODY() {
        final String name = RandomStringUtils.randomAlphanumeric(10);
        final String color = RandomStringUtils.randomAlphanumeric(10);
        final Long distance = 10L;

        StationResponse 상행역 = Station.랜덤역생성();
        StationResponse 하행역 = Station.랜덤역생성();

        return new Section.RequestBody(상행역.getId(), 하행역.getId(), distance);
    }

    public static Section.RequestBody REQUEST_BODY(Long upStationId, Long downStationId) {
        final String name = RandomStringUtils.randomAlphanumeric(10);
        final String color = RandomStringUtils.randomAlphanumeric(10);
        final Long distance = 10L;
        return new Section.RequestBody(upStationId, downStationId, distance);
    }
}
