package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;

public class HttpResponseUtils {

    /**
     * 주어진 응답값으로부터 추출된 Location 속성에서 ID를 반환
     *
     * @param createResponse 응답값
     * @return 추출된 Location 속성의 ID
     */
    public static Long getCreatedLocationId(ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header(HttpHeaders.LOCATION)
                .substring(createResponse.header(HttpHeaders.LOCATION).lastIndexOf('/') + 1));
    }
}
