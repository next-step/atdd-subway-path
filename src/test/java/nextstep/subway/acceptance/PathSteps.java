package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static nextstep.subway.fixture.FieldFixture.경로_내_역_아이디_목록;
import static nextstep.subway.fixture.FieldFixture.경로_조회_도착지_아이디;
import static nextstep.subway.fixture.FieldFixture.경로_조회_출발지_아이디;
import static nextstep.subway.fixture.FieldFixture.노선_간_거리;
import static org.assertj.core.api.Assertions.assertThat;

public class PathSteps {

    public static ExtractableResponse<Response> 지하철_경로_조회_요청(Long sourceId, Long targetId) {
        return given().log().all()
                .queryParam(경로_조회_출발지_아이디.필드명(), sourceId)
                .queryParam(경로_조회_도착지_아이디.필드명(), targetId)
                .when().get("")
                .then().log().all().extract();
    }

    public static void 경로_조회가_성공한다(ExtractableResponse<Response> 지하철_경로_조회_결과) {
        assertThat(지하철_경로_조회_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 경로에_역이_순서대로_포함되어있다(ExtractableResponse<Response> 지하철_경로_조회_결과, Long... 역_id_목록) {
        assertThat(지하철_경로_조회_결과.jsonPath().getList(경로_내_역_아이디_목록.필드명()))
                .containsExactly(역_id_목록);
    }

    public static void 경로의_총_거리가_일치한다(ExtractableResponse<Response> 지하철_경로_조회_결과, int 총_구간거리) {
        assertThat(지하철_경로_조회_결과.jsonPath().getLong(노선_간_거리.필드명()))
                .isEqualTo(총_구간거리);
    }
}
