package nextstep.subway.acceptance;

import static nextstep.subway.fixture.given.LineRequestFixture.distance;
import static nextstep.subway.fixture.given.LineRequestFixture.red;
import static nextstep.subway.fixture.given.LineRequestFixture.신분당선;
import static nextstep.subway.fixture.given.SectionRequestFixture.구간거리;
import static nextstep.subway.fixture.given.StationModifyRequestFixture.또다른지하철역이름;
import static nextstep.subway.fixture.given.StationModifyRequestFixture.새로운지하철역이름;
import static nextstep.subway.fixture.given.StationModifyRequestFixture.지하철역이름;
import static nextstep.subway.fixture.then.ApiStatusFixture.API_삭제_응답코드_검사;
import static nextstep.subway.fixture.then.ApiStatusFixture.API_생성_응답코드_검사;
import static nextstep.subway.fixture.then.ApiStatusFixture.API_잘못된요청_응답코드_검사;
import static nextstep.subway.fixture.then.SectionThenFixture.노선구간추가_상행역설정_오류_검사;
import static nextstep.subway.fixture.then.SectionThenFixture.노선구간추가_하행역이_이미존재할떄_오류_검사;
import static nextstep.subway.fixture.then.SectionThenFixture.삭제할_노선_구간_1개인경우_에러;
import static nextstep.subway.fixture.then.SectionThenFixture.삭제할_노선_구간이_하행종점역이_아닐경우_에러;
import static nextstep.subway.fixture.then.SectionThenFixture.지하철_노선_조회시_구간포함_확인;
import static nextstep.subway.fixture.then.SectionThenFixture.지하철_노선_조회시_해당구간_불포함_확인;
import static nextstep.subway.fixture.when.LineApiFixture.지하철역_노선_등록_요청_후_id_추출;
import static nextstep.subway.fixture.when.SectionApiFixture.지하철_노선_구간_삭제;
import static nextstep.subway.fixture.when.SectionApiFixture.지하철_노선_구간_추가_등록;
import static nextstep.subway.fixture.when.StationApiFixture.지하철역_생성_요청;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTestConfig {

    @DisplayName("지하철 노선 구간 등록 (성공)")
    @Test
    void createSection() {

        //given
        long 상행역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 하행역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");
        long 추가_하행역_id = 지하철역_생성_요청(또다른지하철역이름).jsonPath().getLong("id");
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            지하철역_노선_id, 추가_하행역_id, 하행역_id, 구간거리
        );

        //then
        API_생성_응답코드_검사(response);
        지하철_노선_조회시_구간포함_확인(지하철역_노선_id, 상행역_id, 하행역_id, 추가_하행역_id);

    }

    @DisplayName("지하철 노선 구간 등록 시 추가 구간 상행역이, 노선 하행역이 아니면 등록되지 않아야한다.")
    @Test
    void createSection_NotValidEndStation() {

        //given
        long 상행역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 하행역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");
        long 추가_하행역_id = 지하철역_생성_요청(또다른지하철역이름).jsonPath().getLong("id");
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            지하철역_노선_id, 추가_하행역_id, 상행역_id, 구간거리
        );

        //then
        API_잘못된요청_응답코드_검사(response);
        노선구간추가_상행역설정_오류_검사(response);
    }

    @DisplayName("지하철 노선 구간 등록 시 추가 구간 하행역이 구간에 존재하는 하행역이면 에러")
    @Test
    void createSection_existEndStation() {

        //given
        long 상행역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 하행역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            지하철역_노선_id, 하행역_id, 하행역_id, 구간거리
        );

        //then
        API_잘못된요청_응답코드_검사(response);
        노선구간추가_하행역이_이미존재할떄_오류_검사(response);
    }

    @DisplayName("지하철 구간 삭제 (성공)")
    @Test
    void deleteRemove() {

        //given
        long 상행역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 하행역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);
        long 추가_하행역_id = 지하철역_생성_요청(또다른지하철역이름).jsonPath().getLong("id");
        지하철_노선_구간_추가_등록(지하철역_노선_id, 추가_하행역_id, 하행역_id, 구간거리);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(지하철역_노선_id, 추가_하행역_id);

        //then
        API_삭제_응답코드_검사(response);
        지하철_노선_조회시_해당구간_불포함_확인(지하철역_노선_id, 추가_하행역_id);
    }

    @DisplayName("지하철 구간 삭제 시 하행 종점역이 아니면 삭제할 수 없다.")
    @Test
    void doesNotDeleteSectionWhenDownEndStation() {

        //given
        long 상행역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 하행역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);
        long 추가_하행역_id = 지하철역_생성_요청(또다른지하철역이름).jsonPath().getLong("id");
        지하철_노선_구간_추가_등록(지하철역_노선_id, 추가_하행역_id, 하행역_id, 구간거리);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(지하철역_노선_id, 상행역_id);

        //then
        API_잘못된요청_응답코드_검사(response);
        삭제할_노선_구간이_하행종점역이_아닐경우_에러(response);

    }

    @DisplayName("지하철 구간 삭제 시  구간이 1개인 경우 역을 삭제할 수 없다.")
    @Test
    void doesNotDeleteSectionWhenSectionOnlyOne() {

        //given
        long 상행역_id = 지하철역_생성_요청(지하철역이름).jsonPath().getLong("id");
        long 하행역_id = 지하철역_생성_요청(새로운지하철역이름).jsonPath().getLong("id");
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(지하철역_노선_id, 하행역_id);

        //then
        API_잘못된요청_응답코드_검사(response);
        삭제할_노선_구간_1개인경우_에러(response);

    }
}
