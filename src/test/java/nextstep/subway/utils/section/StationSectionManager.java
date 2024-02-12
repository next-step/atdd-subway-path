package nextstep.subway.utils.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * 지하철역 구간 관리자 유틸 클래스
 */
public class StationSectionManager {

    public static ExtractableResponse<Response> save(long lineId, SectionRequest sectionRequest) {
        return RestAssured
                .given()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static void saveFailure(long lineId, SectionRequest sectionRequest) {
        RestAssured
                .given()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void remove(long lineId, long stationId) {
        RestAssured
                .given()
                .when().delete("lines/{lineId}/sections/{stationId}", lineId, stationId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    public static void removeFailure(long lineId, long stationId) {
        RestAssured
                .given()
                .when().delete("lines/{lineId}/sections/{stationId}", lineId, stationId)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
