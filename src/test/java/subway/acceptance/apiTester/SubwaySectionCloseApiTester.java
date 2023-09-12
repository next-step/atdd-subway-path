package subway.acceptance.apiTester;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import subway.application.response.StationResponse;
import subway.application.response.SubwayLineResponse;
import subway.acceptance.utils.ApiTester;
import subway.acceptance.utils.TestParam;

@ApiTester
public class SubwaySectionCloseApiTester {

    private final String URL = "/subway-lines/{subway-line-id}/sections";

    private ExtractableResponse<Response> request(Long subwayLineId,Long stationId) {

        TestParam param = TestParam.builder()
                .add("stationId", stationId);

        return RestAssured
                .given().log().all()
                .params(param.build())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(URL, subwayLineId)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> 노선에_구간을_비활성화한다(SubwayLineResponse subwayLine, StationResponse station) {
        Long subwayLineId = subwayLine.getId();
        Long stationId = station.getId();

        return request(subwayLineId, stationId);
    }

    public void 비활성화하는_구간의_하행역이_하행_종점역이_아니면_에러_발생(ExtractableResponse<Response> response) {

        Assertions.assertThat(response.statusCode()).isEqualTo(400);

    }

    public void 구간이_하나뿐인_노선은_비활성화_시도_시에_에러_발생(ExtractableResponse<Response> response) {
        Assertions.assertThat(response.statusCode()).isEqualTo(400);

    }

}
