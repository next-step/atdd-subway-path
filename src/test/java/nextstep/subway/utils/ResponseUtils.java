package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;

public class ResponseUtils {

    public static int getInt(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getInt(path);
    }

    public static Long getLong(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getLong(path);
    }

    public static String getString(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getString(path);
    }

    public static List<Long> getLongList(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getList(path, Long.class);
    }

    public static List<String> getStringList(ExtractableResponse<Response> response, String path) {
        return response.jsonPath().getList(path, String.class);
    }
}
