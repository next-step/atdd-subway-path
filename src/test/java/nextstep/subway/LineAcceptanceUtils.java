package nextstep.subway;

import static nextstep.subway.JsonParser.아이디;
import static nextstep.subway.JsonParser.아이디_리스트;
import static nextstep.subway.JsonParser.이름;
import static nextstep.subway.JsonParser.이름_리스트;
import static nextstep.subway.JsonParser.컬러;
import static nextstep.subway.JsonParser.컬러_리스트;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class LineAcceptanceUtils {

    static void 노선_등록_결과_검증(ExtractableResponse<Response> 노선, List<String> 예상_스테이션_이름_리스트) {
        노선 = 지하철_노선_조회한다(아이디(노선));
        List<String> 스테이션_이름_리스트 = 스테이션_이름_리스트(노선);
        assertThat(스테이션_이름_리스트).isEqualTo(예상_스테이션_이름_리스트);
    }

    static void 구간_제거_실패_검증(ExtractableResponse<Response> 노선에서_구간_제거_결과) {
        assertAll(
                () -> assertThat(노선에서_구간_제거_결과.statusCode()).isEqualTo(
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    static void 자하철노선_목록_조회_검증(int 강남역_아이디, int 판교역_아이디, int 사당역_아이디, ExtractableResponse<Response> 신분당선,
            ExtractableResponse<Response> _2호선, ExtractableResponse<Response> 지하철_노선_목록) {
        assertAll(
                () -> assertThat(아이디_리스트(지하철_노선_목록)).contains(아이디(신분당선), 아이디(_2호선)),
                () -> assertThat(이름_리스트(지하철_노선_목록)).contains("신분당선", "2호선"),
                () -> assertThat(컬러_리스트(지하철_노선_목록)).contains("bg-red-600", "bg-red-500"),
                () -> assertThat(스테이션_아이디_리스트(지하철_노선_목록, 0)).contains(강남역_아이디, 판교역_아이디),
                () -> assertThat(스테이션_이름_리스트(지하철_노선_목록, 0)).contains("강남역", "판교역"),
                () -> assertThat(스테이션_아이디_리스트(지하철_노선_목록, 1)).contains(강남역_아이디, 사당역_아이디),
                () -> assertThat(스테이션_이름_리스트(지하철_노선_목록, 1)).contains("강남역", "사당역")
        );
    }

    static void 지하철노선_생성_검증(int 강남역_아이디, int 판교역_아이디, ExtractableResponse<Response> 등록된_지하철노선) {
        assertAll(
                () -> assertThat(이름(등록된_지하철노선)).isEqualTo("신분당선"),
                () -> assertThat(컬러(등록된_지하철노선)).isEqualTo("bg-red-600"),
                () -> assertThat(스테이션_아이디_리스트(등록된_지하철노선)).contains(강남역_아이디, 판교역_아이디),
                () -> assertThat(스테이션_이름_리스트(등록된_지하철노선)).contains("강남역", "판교역")
        );
    }

    static void 지하철노선_조회_검증(int 강남역_아이디, int 판교역_아이디, int 신분당선_아이디, ExtractableResponse<Response> 조회된_지하철노선) {
        assertAll(
                () -> assertThat(아이디(조회된_지하철노선)).isEqualTo(신분당선_아이디),
                () -> assertThat(이름(조회된_지하철노선)).isEqualTo("신분당선"),
                () -> assertThat(컬러(조회된_지하철노선)).isEqualTo("bg-red-600"),
                () -> assertThat(스테이션_아이디_리스트(조회된_지하철노선)).contains(강남역_아이디, 판교역_아이디),
                () -> assertThat(스테이션_이름_리스트(조회된_지하철노선)).contains("강남역", "판교역")
        );
    }

    static void 지하철노선_수정_검증(int 수정될_지하철_노선_아이디) {
        var 수정된_지하철_노선 = 지하철_노선_조회한다(수정될_지하철_노선_아이디);
        assertAll(
                () -> assertThat(이름(수정된_지하철_노선)).isEqualTo("다른분당선"),
                () -> assertThat(컬러(수정된_지하철_노선)).isEqualTo("bg-red-500")
        );
    }

    static void 지하철노선_삭제_검증(int 신분당선_아이디) {
        var 지하철_노선_목록 = 지하철_노선_목록_조회한다();
        assertAll(
                () -> assertThat(아이디_리스트(지하철_노선_목록)).doesNotContain(신분당선_아이디),
                () -> assertThat(이름_리스트(지하철_노선_목록)).doesNotContain("신분당선")
        );
    }

    static void 구간_등록_성공_검증(int 강남역_아이디, int 판교역_아이디, int 정자역_아이디, int 신분당선_아이디) {
        var 변경된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(스테이션_아이디_리스트(변경된_신분당선))
                        .contains(강남역_아이디, 판교역_아이디, 정자역_아이디),
                () -> assertThat(스테이션_이름_리스트(변경된_신분당선))
                        .contains("강남역", "판교역", "정자역")
        );
    }

    static void 중간역_제거후_재비치_검증(int 강남역_아이디, int 정자역_아이디, int 신분당선_아이디) {
        var 조회된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(스테이션_이름_리스트(조회된_신분당선)).containsExactly("강남역", "정자역"),
                () -> assertThat(스테이션_아이디_리스트(조회된_신분당선)).contains(강남역_아이디, 정자역_아이디)
        );
    }

    static void 역_제거_검증(ExtractableResponse<Response> 노선에서_구간_제거_결과, int 노선_아이디, String 제거된_역이름) {
        var 조회된_신분당선 = 지하철_노선_조회한다(노선_아이디);
        assertAll(
                () -> assertThat(노선에서_구간_제거_결과.statusCode()).isEqualTo(
                        HttpStatus.NO_CONTENT.value()),
                () -> assertThat(스테이션_이름_리스트(조회된_신분당선)).doesNotContain(제거된_역이름)
        );
    }

    static void 신분당선_판교역_제거_실패_검증(int 신분당선_아이디, ExtractableResponse<Response> 노선에서_구간_제거_결과) {
        var 조회된_신분당선 = 지하철_노선_조회한다(신분당선_아이디);
        assertAll(
                () -> assertThat(노선에서_구간_제거_결과.statusCode()).isEqualTo(
                        HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(스테이션_이름_리스트(조회된_신분당선)).contains("판교역")
        );
    }

    static void 경로_조회_결과_검증(ExtractableResponse<Response> 경로_조회_결과, List<String> 예상_스테이션_이름_리스트, int 예상_거리) {
        Assertions.assertAll(
                () -> assertThat(스테이션_이름_리스트(경로_조회_결과)).isEqualTo(예상_스테이션_이름_리스트),
                () -> assertThat(경로_조회_결과.jsonPath().getInt("distance")).isEqualTo(예상_거리)
        );
    }

    static ExtractableResponse<Response> 경로_조회(int 출발역, int 도착역) {
        Map<String, Object> params = new HashMap<>();
        params.put("source", 출발역);
        params.put("target", 도착역);
        return RestAssured.given().params(params)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_조회한다(int 지하철노선_아이디) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + 지하철노선_아이디)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_등록한다(String 노선이름, String 노선색상,
            int 상행종점역, int 하행종점역, int 거리) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", 노선이름);
        params.put("color", 노선색상);
        params.put("upStationId", 상행종점역);
        params.put("downStationId", 하행종점역);
        params.put("distance", 거리);

        return RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(params).log()
                .all()
                .when().post("/lines")
                .then().log().all()
                .extract();
    }


    static ExtractableResponse<Response> 지하철_노선_목록_조회한다() {
        return RestAssured
                .given().accept(ContentType.JSON).log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_수정한다(int 수정될_지하철_노선_아이디, String 수정될_이름,
            String 수정될_컬러) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", 수정될_이름);
        params.put("color", 수정될_컬러);

        return RestAssured
                .given().accept(ContentType.JSON).contentType(ContentType.JSON).body(params)
                .when().put("/lines/" + 수정될_지하철_노선_아이디)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_삭제한다(int 신분당선_아이디) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + 신분당선_아이디)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 노선_구간을_등록한다(int 노선_아이디, int 상행역, int 하행역,
            int 거리) {
        Map<String, Object> params = new HashMap<>();
        params.put("downStationId", 하행역);
        params.put("upStationId", 상행역);
        params.put("distance", 거리);
        return RestAssured
                .given().body(params).log().all()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when().post("/lines/" + 노선_아이디 + "/sections")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 노선에서_구간_제거한다(int 종점역_아이디, int 노선_아이디) {
        return RestAssured
                .given().log().all().param("stationId", 종점역_아이디)
                .when().delete("/lines/" + 노선_아이디 + "/sections")
                .then().log().all()
                .extract();
    }

    private static List<String> 스테이션_이름_리스트(ExtractableResponse<Response> 지하철_노선_목록, int 위치) {
        return 지하철_노선_목록.jsonPath().getList("stations[" + 위치 + "].name", String.class);
    }

    private static List<Integer> 스테이션_아이디_리스트(ExtractableResponse<Response> 지하철_노선_목록, int 위치) {
        return 지하철_노선_목록.jsonPath().getList("stations[" + 위치 + "].id", Integer.class);
    }

    private static List<String> 스테이션_이름_리스트(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.name", String.class);
    }

    private static List<Integer> 스테이션_아이디_리스트(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations.id", Integer.class);
    }

}
