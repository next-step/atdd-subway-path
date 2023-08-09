package subway.acceptance.steps;

import static subway.factory.SubwayNameFactory.강남역;
import static subway.factory.SubwayNameFactory.광교역;
import static subway.factory.SubwayNameFactory.논현역;
import static subway.factory.SubwayNameFactory.수서역;
import static subway.factory.SubwayNameFactory.신논현역;
import static subway.factory.SubwayNameFactory.양재역;

import io.restassured.response.ValidatableResponse;
import subway.acceptance.utils.RestAssuredClient;
import subway.dto.LineRequest;
import subway.dto.StationRequest;

public class LineSteps {

    private static final String PATH = "/lines";

    public static void 지하철역_생성() {
        String stationPath = "/stations";
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(강남역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(광교역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(양재역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(논현역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(신논현역).build());
        RestAssuredClient.requestPost(stationPath, StationRequest.builder().name(수서역).build());
    }

    public static ValidatableResponse 노선_조회() {
        return RestAssuredClient.requestGet(PATH);
    }

    public static ValidatableResponse 노선_생성(LineRequest params) {
        return RestAssuredClient.requestPost(PATH, params);
    }

    public static ValidatableResponse 노선_수정(Long id, LineRequest params) {
        return RestAssuredClient.requestPut(PATH + "/" + id, params);
    }

    public static ValidatableResponse 노선_삭제(Long id) {
        return RestAssuredClient.requestDelete(PATH + "/" + id);
    }
}
