package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTestHelper {

    public static List<StationResponse> selectAllStations() {
        // when
        ExtractableResponse<Response> response =
                RestAssured.given().
                            log().all()
                        .when()
                            .get("/stations")
                        .then()
                            .log().all()
                            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        return response.body().as(new TypeRef<>() {});
    }

    public static StationResponse createStation(String name) {
        ExtractableResponse<Response> response =
                RestAssured.given()
                            .log().all()
                            .body(Map.of("name", name))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().
                            post("/stations")
                        .then()
                            .log().all()
                            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response.body().as(StationResponse.class);
    }
}
