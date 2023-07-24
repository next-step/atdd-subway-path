package nextstep.subway.acceptance;

import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.distance;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.green;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.red;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.다른분당선;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.분당선;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.새로운지하철역_id;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.신분당선;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.지하철역_id;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.노선수정요청_데이터_생성;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.또다른지하철역이름;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.새로운지하철역이름;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.지하철역이름;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_생성_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_요청성공_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_잘못된요청_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선_응답값_id_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선_응답값_노선색_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선_응답값_노선이름_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선_응답값_포함된_지하철역_크기_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선_응답값_포함된지하철역_id_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선목록_노선이름_포함_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선목록_조회시_생성한노선_id_포함_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.노선목록_크기_검사;
import static nextstep.subway.fixture.acceptance.then.LineThenFixture.없는노선조회시_에러표출_검사;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_단건_조회;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_등록_요청;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_등록_요청_후_id_추출;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_목록_조회_요청;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_삭제;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_수정;
import static nextstep.subway.fixture.acceptance.when.StationApiFixture.지하철역_생성_요청;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTestConfig {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        //given
        long 지하철역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 새로운지하철역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> 노선등록응답값 = 지하철역_노선_등록_요청(신분당선, red, 지하철역_id, 새로운지하철역_id, distance);

        //then
        assertAll(
            () -> API_생성_응답코드_검사(노선등록응답값),
            () -> 노선_응답값_id_검사(노선등록응답값, 1L),
            () -> 노선_응답값_노선이름_검사(노선등록응답값, 신분당선),
            () -> 노선_응답값_노선색_검사(노선등록응답값, red),
            () -> 노선_응답값_포함된_지하철역_크기_검사(노선등록응답값, 2),
            () -> 노선_응답값_포함된지하철역_id_검사(노선등록응답값, 지하철역_id, 새로운지하철역_id),
            () -> 노선목록_조회시_생성한노선_id_포함_검사(노선등록응답값)
        );

    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getStationLines() {

        //given
        long 지하철역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 새로운지하철역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");
        long 또다른지하철역_id = 지하철역_생성_요청(또다른지하철역이름).jsonPath().getLong("id");

        지하철역_노선_등록_요청(신분당선, red, 지하철역_id, 새로운지하철역_id, distance);
        지하철역_노선_등록_요청(분당선, green, 지하철역_id, 또다른지하철역_id, distance);

        //when
        ExtractableResponse<Response> 노선목록조회결과_응답값 = 지하철역_노선_목록_조회_요청();

        //then
        assertAll(
            () -> API_요청성공_응답코드_검사(노선목록조회결과_응답값),
            () -> 노선목록_크기_검사(노선목록조회결과_응답값, 2),
            () -> 노선목록_노선이름_포함_검사(노선목록조회결과_응답값, List.of(신분당선, 분당선))
        );
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getStationLine() {

        //given
        long 지하철역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 새로운지하철역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");

        long 신규등록_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 지하철역_id, 새로운지하철역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철역_노선_단건_조회(신규등록_노선_id);

        //then
        assertAll(
            () -> API_요청성공_응답코드_검사(response),
            () -> 노선_응답값_id_검사(response, 1L),
            () -> 노선_응답값_노선이름_검사(response, 신분당선),
            () -> 노선_응답값_노선색_검사(response, red),
            () -> 노선_응답값_포함된_지하철역_크기_검사(response, 2),
            () -> 노선_응답값_포함된지하철역_id_검사(response, 지하철역_id, 새로운지하철역_id)
        );

    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyStationLine() {

        //given
        지하철역_생성_요청(지하철역이름);
        지하철역_생성_요청(새로운지하철역이름);
        long 신규등록_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 지하철역_id, 새로운지하철역_id, distance);

        //when
        지하철역_노선_수정(신규등록_노선_id, 노선수정요청_데이터_생성(다른분당선, red));

        //then
        ExtractableResponse<Response> 노선조회응답값 = 지하철역_노선_단건_조회(신규등록_노선_id);

        assertAll(
            () -> API_요청성공_응답코드_검사(노선조회응답값),
            () -> 노선_응답값_노선이름_검사(노선조회응답값, 다른분당선),
            () -> 노선_응답값_노선색_검사(노선조회응답값, red)
        );
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteStationLine() {

        //given
        지하철역_생성_요청(지하철역이름);
        지하철역_생성_요청(새로운지하철역이름);
        long 신규등록_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 지하철역_id, 새로운지하철역_id, distance);

        //when
        지하철역_노선_삭제(신규등록_노선_id);

        //then
        ExtractableResponse<Response> response = 지하철역_노선_단건_조회(신규등록_노선_id);

        assertAll(
            () -> API_잘못된요청_응답코드_검사(response),
            () -> 없는노선조회시_에러표출_검사(response)
        );
    }

}
