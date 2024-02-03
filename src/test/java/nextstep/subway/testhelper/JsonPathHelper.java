package nextstep.subway.testhelper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;

public class JsonPathHelper {

    public static <T> T getObject(ExtractableResponse<Response> response,
                                  String path,
                                  Class<T> t) {
        return response.jsonPath().getObject(path, t);
    }

    public static <T> List<T> getAll(ExtractableResponse<Response> response,
                                     String path,
                                     Class<T> t) {
        return response.jsonPath().getList(path, t);
    }

}
