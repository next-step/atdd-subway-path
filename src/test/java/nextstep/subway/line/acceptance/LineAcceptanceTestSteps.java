package nextstep.subway.line.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class LineAcceptanceTestSteps {

  public static Map<String,String> createLineRequestParams(String name, LineColor color,long startId, long endId) {
    Map params = new HashMap<>();
    params.put("name",name);
    params.put("color",LineColor.of(color));
    params.put("upStationId",startId);
    params.put("downStationId",endId);
    return params;
  }

  public static ExtractableResponse<Response> 지하철_노선_생성요청(String name, LineColor color,long startId, long endId) {
    Map<String,String> params = createLineRequestParams(name,color,startId,endId);

    ExtractableResponse<Response> response =  RestAssured.given().log().all()
        .body(params)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/lines")
        .then()
        .log().all()
        .extract();


    return response;

  }

  public static void 지하철_노선_수정됨(ExtractableResponse<Response> response,LineColor lineColor) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.ACCEPTED.value());
    assertThat(response.as(LineResponse.class).getColor()).isEqualTo(LineColor.of(lineColor));
  }

  public static void 지하철_노선목록_조회됨(ExtractableResponse<Response> response, String lineName) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.body().asString()).contains(lineName);
  }

  public static void 지하철_노선_조회됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
  }

  public static void 지하철_노선에서_구간_조회됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.body().jsonPath().getList("stations").size()).isNotEqualTo(0);
  }

  public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }
  public static void 지하철_노선생성_실패됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }
  public static void 지하철_노선삭제됨(ExtractableResponse<Response> response){
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  public static ExtractableResponse<Response> 지하철_노선목록조회_요청(){
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().get("/lines")
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> 지하철_노선조회_요청(long id){
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().get("/lines/{id}",id)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> 지하철_노선수정_요청(Map params,long id){
    return RestAssured
        .given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE).body(params)
        .when().put("/lines/{id}",id)
        .then().log().all().extract();
  }

  public static ExtractableResponse<Response> 지하철_노선삭제_요청(long id) {
    return RestAssured
        .given().log().all()
        .when().delete("/lines/{id}",id)
        .then().log().all().extract();
  }
  public static void 노선_구간_등록됨(ExtractableResponse<Response> response){
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }
  public static void 노선_구간_등록실패됨(ExtractableResponse<Response> response){
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  public static void 노선_구간_삭제됨(ExtractableResponse<Response> response){
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  public static void 노선_구간_삭제실패됨(ExtractableResponse<Response> response) {
    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }
  public static  ExtractableResponse<Response> 구간등록요청(long lineId,long upStationId, long downStationId,int distance){

    Map params = new HashMap<>();
    params.put("upStationId",upStationId);
    params.put("downStationId",downStationId);
    params.put("distance",distance);

    return RestAssured.given().log().all()
        .body(params)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .post("/lines/{lineId}/sections",lineId)
        .then().log().all()
        .extract();
  }

  public static ExtractableResponse<Response> 구간삭제요청(long lineId,long downStationId){
    return RestAssured.given().log().all()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .delete("/lines/{lineId}/sections?stationId={downStationId}",lineId,downStationId)
        .then().log().all()
        .extract();
  }

}
