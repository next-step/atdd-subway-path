package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;

public class ResponseUtils {

    public static Long getId(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    public static String getName(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }

    public static String getString(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getString(path);
    }

    public static List<Long> getLongList(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getList("stations.id", Long.class);
    }

    public static List<String> getNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    public static List<String> getStringList(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getList(path, String.class);
    }
}
