package nextstep.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

public class JsonParser {


    public static int 아이디(ExtractableResponse<Response> response) {
        return response.jsonPath().getInt("id");
    }


    public static String 이름(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }


    public static String 컬러(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("color");
    }

    public static List<Integer> 아이디_리스트(ExtractableResponse<Response> 지하철_노선_목록) {
        return 지하철_노선_목록.jsonPath().getList("id", Integer.class);
    }

    public static List<String> 이름_리스트(ExtractableResponse<Response> 지하철_노선_목록) {
        return 지하철_노선_목록.jsonPath().getList("name", String.class);
    }

    public static List<String> 컬러_리스트(ExtractableResponse<Response> 지하철_노선_목록) {
        return 지하철_노선_목록.jsonPath().getList("color", String.class);
    }
}
