package nextstep.subway.document;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.Documentation;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;

import static nextstep.subway.station.acceptance.StationSteps.지하철역_등록되어_있음;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class LineDocumentation extends Documentation {
    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineRequest lineRequest;
    private LineUpdateRequest lineUpdateRequest;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        super.setUp(restDocumentation);

        // given
        강남역 = 지하철역_등록되어_있음(로그인_사용자, "강남역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음(로그인_사용자, "광교역").as(StationResponse.class);
        lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10, 10);
        lineUpdateRequest = new LineUpdateRequest("구분당선", "bg-old-600");
    }

    @Test
    void line() {
        ExtractableResponse<Response> lineResponse = RestAssured
                .given(spec).log().all()
                .filter(document("line/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + 로그인_사용자.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all().extract();

        RestAssured
                .given(spec).log().all()
                .filter(document("line/list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + 로그인_사용자.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();

        String location = lineResponse.header("Location");
        RestAssured
                .given(spec).log().all()
                .filter(document("line/read",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + 로그인_사용자.getAccessToken())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(location)
                .then().log().all().extract();

        RestAssured
                .given(spec).log().all()
                .filter(document("line/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + 로그인_사용자.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineUpdateRequest)
                .when().put(location)
                .then().log().all().extract();

        RestAssured
                .given(spec).log().all()
                .filter(document("line/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + 로그인_사용자.getAccessToken())
                .when().delete(location)
                .then().log().all().extract();
    }
}
