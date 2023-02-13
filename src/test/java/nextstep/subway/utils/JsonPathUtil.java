package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.fixture.FieldFixture;

import java.util.List;

public class JsonPathUtil {

    public static Long Long으로_추출(ExtractableResponse<Response> API_응답_결과, FieldFixture 추출할_데이터_필드) {
        return API_응답_결과.jsonPath().getLong(추출할_데이터_필드.필드명());
    }

    public static String 문자열로_추출(ExtractableResponse<Response> API_응답_결과, FieldFixture 추출할_데이터_필드) {
        return API_응답_결과.jsonPath().getString(추출할_데이터_필드.필드명());
    }

    public static <T> List<T> 리스트로_추출(ExtractableResponse<Response> API_응답_결과, FieldFixture 추출할_데이터_필드, Class<T> classType) {
        return API_응답_결과.jsonPath().getList(추출할_데이터_필드.필드명(), classType);
    }
}
