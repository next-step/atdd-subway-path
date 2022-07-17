package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;

public class ResponseUtils {

    public static Long getId(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static List<Long> getStationIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    public static List<String> getStationNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }
}
