package nextstep.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.view.LineCreateRequest;
import nextstep.subway.line.view.LineResponse;

public class LineFixture {
    private static final String API_CREATE_LINE = "/lines";

    public LineResponse 노선생성(String name, String color, long upStationId, long downStationId, int distance) {
        LineCreateRequest request = new LineCreateRequest(name, color, upStationId, downStationId, distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                          .body(request)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post(API_CREATE_LINE)
                          .then().log().all()
                          .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        return response.as(LineResponse.class);
    }

}
