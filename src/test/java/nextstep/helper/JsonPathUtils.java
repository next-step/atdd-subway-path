package nextstep.helper;

import io.restassured.response.ResponseBodyExtractionOptions;

import java.util.List;

public class JsonPathUtils {

    public static String getStringPath(
        ResponseBodyExtractionOptions responseBody,
        String pathName
    ) {
        return responseBody.jsonPath().getString(pathName);
    }

    public static Integer getIntPath(
        ResponseBodyExtractionOptions responseBody,
        String pathName
    ) {
        return responseBody.jsonPath().getInt(pathName);
    }

    public static Long getLongPath(
        ResponseBodyExtractionOptions responseBody,
        String pathName
    ) {
        return responseBody.jsonPath().getLong(pathName);
    }

    public static <T> List<T> getListPath(
        ResponseBodyExtractionOptions responseBody,
        String pathName,
        Class<T> elementType
    ) {
        return responseBody.jsonPath().getList(pathName, elementType);
    }
}
