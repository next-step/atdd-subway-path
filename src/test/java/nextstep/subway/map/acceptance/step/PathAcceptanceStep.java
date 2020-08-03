package nextstep.subway.map.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.map.dto.SearchType;
import org.springframework.http.MediaType;

public class PathAcceptanceStep {

    public static ExtractableResponse<Response> 출발역에서_도착역까지의_최단거리_경로_조회를_요청(Long sourceId, Long targetId) {
        return RestAssured.
                given().
                log().all().
                accept(MediaType.APPLICATION_JSON_VALUE).
                        when().
                        get("/paths?source={sourceId}&target={targetId}&type={type}", sourceId, targetId, SearchType.DISTANCE).
                        then().
                        log().all().
                        extract();
    }

    public static ExtractableResponse<Response> 출발역에서_도착역까지의_최소_시간_경로_조회를_요청(Long sourceId, Long targetId) {
        return RestAssured.
                given().
                log().all().
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/paths?source={sourceId}&target={targetId}&type={type}", sourceId, targetId, SearchType.DURATION).
                then().
                log().all().
                extract();
    }
}
