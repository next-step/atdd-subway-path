package nextstep.subway.acceptance;

import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.강남역;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.새로운지하철역이름;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.지하철역이름;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_삭제_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_생성_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_요청성공_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.StationThenFixture.지하철역_목록_리스트_크기_검사;
import static nextstep.subway.fixture.acceptance.then.StationThenFixture.지하철역_목록_조회_지하철역_이름_검사;
import static nextstep.subway.fixture.acceptance.then.StationThenFixture.지하철역_목록_조회시_생성한역을_포함하는지_검사;
import static nextstep.subway.fixture.acceptance.then.StationThenFixture.지하철역_목록_조회시_아무런값도_조회되지않음_검사;
import static nextstep.subway.fixture.acceptance.when.StationApiFixture.지하철역_리스트_조회;
import static nextstep.subway.fixture.acceptance.when.StationApiFixture.지하철역_삭제;
import static nextstep.subway.fixture.acceptance.when.StationApiFixture.지하철역_생성_요청;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTestConfig {

    private static final int 첫번째 = 0;
    private static final int 두번째 = 1;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 응답결과 = 지하철역_생성_요청(강남역);

        // then
        API_생성_응답코드_검사(응답결과);
        지하철역_목록_조회시_생성한역을_포함하는지_검사(강남역);
    }

    @Test
    @DisplayName("지하철역 목록을 조회한다.")
    void showStations() {

        //given
        지하철역_생성_요청(지하철역이름);
        지하철역_생성_요청(새로운지하철역이름);

        //when
        ExtractableResponse<Response> 응답결과 = 지하철역_리스트_조회();

        //then
        Assertions.assertAll(
            () -> API_요청성공_응답코드_검사(응답결과),
            () -> 지하철역_목록_리스트_크기_검사(응답결과, 2),
            () -> 지하철역_목록_조회_지하철역_이름_검사(응답결과, 지하철역이름, 첫번째),
            () -> 지하철역_목록_조회_지하철역_이름_검사(응답결과, 새로운지하철역이름, 두번째)
        );
    }

    @Test
    @DisplayName("지하철역 삭제")
    void removeStation() {

        //given
        long 신규생성_지하철역_id = 지하철역_생성_요청(새로운지하철역이름)
                .jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> 응답결과 = 지하철역_삭제(신규생성_지하철역_id);

        //then
        API_삭제_응답코드_검사(응답결과);
        지하철역_목록_조회시_아무런값도_조회되지않음_검사();

    }
}