package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.section.SectionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTestHelper {
  public static LineResponse createLine(String name) {
    return createLine(name, "red-bg-600");
  }

  public static LineResponse createLine(String name, String color) {
    return createLine(name, color, null, null, 10);
  }
  
  public static LineResponse createLine(String name, String color, @Nullable Long upStationId, @Nullable Long downStationId, long distance) {
    return createLine(new LineRequest(
        name, color, upStationId, downStationId, distance
    ));
  }

  public static LineResponse createLine(LineRequest lineRequest) {
    ExtractableResponse<Response> response = RestAssured
        .given()
          .log().all()
          .body(lineRequest)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
          .post("/lines")
        .then()
          .log().all()
          .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    return response.body().as(LineResponse.class);
  }

  public static List<LineResponse> selectAllLines() {
    ExtractableResponse<Response> response = RestAssured
        .given()
        .log().all()
        .when()
        .get("/lines")
        .then()
        .log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    return response.body().as(new TypeRef<>() {});
  }

  public static LineResponse selectLine(long id) {
    ExtractableResponse<Response> response = RestAssured
        .given()
        .log().all()
        .when()
        .get("/lines/{id}", id)
        .then()
        .log().all()
        .extract();

    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    return response.body().as(LineResponse.class);
  }

  public static ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, long distance) {
    return RestAssured
        .given()
          .log().all()
          .body(new SectionRequest(upStationId, downStationId, distance))
          .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
          .post("/lines/{id}/sections", lineId)
        .then()
          .log().all()
          .extract();
  }

  public static ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
    return RestAssured
        .given()
          .log().all()
        .when()
          .delete("/lines/{lineId}/sections/{stationId}", lineId, stationId)
        .then()
          .log().all()
          .extract();
  }
}
