package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.dto.ModifyLineRequest;
import org.springframework.http.MediaType;

public class SubwayLineSteps {

    public static CreateLineRequest 지하철노선등록요청_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
        return CreateLineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }

    public static ExtractableResponse<Response> 지하철노선등록요청(CreateLineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선목록조회요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선조회요청(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ModifyLineRequest 지하철노선수정요청_생성(String name, String color) {
        return ModifyLineRequest.builder()
                .name(name)
                .color(color)
                .build();
    }

    public static ExtractableResponse<Response> 지하철노선수정요청(Long id, ModifyLineRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철노선삭제요청(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
