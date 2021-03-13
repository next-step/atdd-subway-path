package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StationVerificationSteps {

    public static void 지하철_역_생성_됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        생선된_지하철_역_URI_경로_존재_함(response);
    }

    public static void 생선된_지하철_역_URI_경로_존재_함(ExtractableResponse<Response> response) {
        assertThat(생성된_지하철_역_URI_경로_확인(response)).isNotBlank();
    }

    public static String 생성된_지하철_역_URI_경로_확인(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    public static void 지하철_역_생성_실패_됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    public static void 지하철_역_목록_조회_됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_역_목록_조회_결과에_생성한_역_포함_확인(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createStationResponses) {
        List<Long> expectedLineIds = 생성한_지하철_역_ID(createStationResponses);
        List<Long> resultLineIds = 지하철_역_목록_조회_결과_ID(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static List<Long> 생성한_지하철_역_ID(List<ExtractableResponse<Response>> createStationResponses) {
        return createStationResponses.stream()
                .map(it -> Long.parseLong(생성된_지하철_역_URI_경로_확인(it).split("/")[2]))
                .collect(Collectors.toList());
    }

    public static List<Long> 지하철_역_목록_조회_결과_ID(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    public static void 지하철_역_제거_됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
