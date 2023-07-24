package subway.acceptance.apiTester;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.springframework.http.MediaType;
import subway.application.query.response.StationResponse;
import subway.application.query.response.SubwayLineResponse;
import subway.common.annotation.ApiTester;
import subway.acceptance.utils.TestParam;

@ApiTester
public class SubwaySectionAddApiTester {

    private final String URL = "/subway-lines/{subway-line-id}/sections";

    private ExtractableResponse<Response> request(Long subwayLineId,Long upStationId, Long downStationId, int distance) {

        TestParam testParam = TestParam.builder()
                .add("upStationId", upStationId)
                .add("downStationId", downStationId)
                .add("distance", distance);

        return RestAssured
                .given().log().all()
                .body(testParam.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post(URL, subwayLineId)
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> 노선에_구간을_추가한다(SubwayLineResponse subwayLine, StationResponse upStation, StationResponse downStation, int distance) {
        Long upStationId = upStation.getId();
        Long downStationId = downStation.getId();
        Long subwayLineId = subwayLine.getId();

        return request(subwayLineId, upStationId, downStationId, distance);
    }

    public void 추가하는_구간의_상행역이_하행_종점역이_아니면_에러_발생(ExtractableResponse<Response> response, SubwayLineResponse subwayLine, StationResponse upStation) {

        Assertions.assertThat(response.statusCode()).isEqualTo(400);

    }

    public void 추가하는_구간의_하행역이_이미_노선에_존재한다면_에러_발생(ExtractableResponse<Response> response, SubwayLineResponse subwayLine, StationResponse downStation) {
        Assertions.assertThat(response.statusCode()).isEqualTo(400);

    }

}
