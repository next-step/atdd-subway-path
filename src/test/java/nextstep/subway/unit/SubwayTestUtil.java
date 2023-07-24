package nextstep.subway.unit;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class SubwayTestUtil {
    public static <T> T toSpecificResponse(ExtractableResponse<Response> response, final Class<T> responseType) {
        return response.as(responseType);
    }
}
