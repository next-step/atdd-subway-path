package nextstep.subway.acceptance;

import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.distance;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.red;
import static nextstep.subway.fixture.acceptance.given.LineRequestFixture.신분당선;
import static nextstep.subway.fixture.acceptance.given.SectionRequestFixture.구간거리;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.또다른지하철역이름;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.새로운지하철역이름;
import static nextstep.subway.fixture.acceptance.given.StationModifyRequestFixture.지하철역이름;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_삭제_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_생성_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.ApiStatusFixture.API_잘못된요청_응답코드_검사;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.노선구간추가시_구간거리가_기존거리보다_같거나_길다면_에러;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.노선구간추가시_모든역이_노선에_이미_존재할때_오류_검사;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.노선구간추가시_모든역이_노선에_존재하지않을때_오류_검사;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.삭제할_노선_구간_1개인경우_에러;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.삭제할_노선_구간이_없는역일경우_에러;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.지하철_노선_조회시_구간_id_순서_검사;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.지하철_노선_조회시_구간포함_확인;
import static nextstep.subway.fixture.acceptance.then.SectionThenFixture.지하철_노선_조회시_해당구간_불포함_확인;
import static nextstep.subway.fixture.acceptance.when.LineApiFixture.지하철역_노선_등록_요청_후_id_추출;
import static nextstep.subway.fixture.acceptance.when.SectionApiFixture.지하철_노선_구간_삭제;
import static nextstep.subway.fixture.acceptance.when.SectionApiFixture.지하철_노선_구간_추가_등록;
import static nextstep.subway.fixture.acceptance.when.StationApiFixture.지하철역_생성_요청_후_id_추출;

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
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 추가_하행역_id = 지하철역_생성_요청_후_id_추출(또다른지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            지하철역_노선_id, 추가_하행역_id, 하행역_id, 구간거리
        );

        //then
        API_생성_응답코드_검사(response);
        지하철_노선_조회시_구간포함_확인(지하철역_노선_id, 상행역_id, 하행역_id, 추가_하행역_id);

    }

    @DisplayName("지하철 노선 구간 사이에 추가 등록")
    @Test
    void addMiddleSection() {

        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 추가역_id = 지하철역_생성_요청_후_id_추출(또다른지하철역이름);

        long 노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            노선_id, 추가역_id, 상행역_id, 3
        );

        //then
        API_생성_응답코드_검사(response);
        지하철_노선_조회시_구간_id_순서_검사(노선_id, 상행역_id, 추가역_id, 하행역_id);
    }

    @DisplayName("지하철 노선 구간 등록 시 상행역과 하행역 둘 다 노선에 포함되어있다면 에러")
    @Test
    void getExceptionAtAddSectionAlreadyStationAll() {

        //given
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            지하철역_노선_id, 상행역_id, 하행역_id, distance - 5
        );

        //then
        API_잘못된요청_응답코드_검사(response);
        노선구간추가시_모든역이_노선에_이미_존재할때_오류_검사(response);
    }

    @DisplayName("지하철 노선 구간 등록 시 상행역과 하행역 둘 다 노선에 포함되어있지 않으면 에러")
    @Test
    void getExceptionAtAddSectionNoneStationAll() {

        //given
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        long 별도역1_id = 지하철역_생성_요청_후_id_추출("별도역1");
        long 별도역2_id = 지하철역_생성_요청_후_id_추출("별도역2");

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            지하철역_노선_id, 별도역1_id, 별도역2_id, distance -5
        );

        //then
        API_잘못된요청_응답코드_검사(response);
        노선구간추가시_모든역이_노선에_존재하지않을때_오류_검사(response);
    }

    @DisplayName("지하철 노선 구간 등록 시 추가 구간 거리가 기존 구간보다 크면 에러")
    @Test
    void createSection_existEndStation() {

        //given
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        long 별도역1_id = 지하철역_생성_요청_후_id_추출("별도역1");

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_추가_등록(
            지하철역_노선_id, 하행역_id, 별도역1_id, distance + 1
        );

        //then
        API_잘못된요청_응답코드_검사(response);
        노선구간추가시_구간거리가_기존거리보다_같거나_길다면_에러(response);
    }

    @DisplayName("지하철 하행 종점 구간 삭제 (성공)")
    @Test
    void deleteRemove() {

        //given
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);
        long 추가_하행역_id = 지하철역_생성_요청_후_id_추출(또다른지하철역이름);
        지하철_노선_구간_추가_등록(지하철역_노선_id, 추가_하행역_id, 하행역_id, 구간거리);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(지하철역_노선_id, 추가_하행역_id);

        //then
        API_삭제_응답코드_검사(response);
        지하철_노선_조회시_해당구간_불포함_확인(지하철역_노선_id, 추가_하행역_id);
    }

    @DisplayName("지하철 라인 구간의 중간역 삭제 (성공)")
    @Test
    void deleteMiddleStation() {

        //given
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);
        long 추가_하행역_id = 지하철역_생성_요청_후_id_추출(또다른지하철역이름);
        지하철_노선_구간_추가_등록(지하철역_노선_id, 추가_하행역_id, 하행역_id, 구간거리);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(지하철역_노선_id, 하행역_id);

        //then
        API_삭제_응답코드_검사(response);
        지하철_노선_조회시_해당구간_불포함_확인(지하철역_노선_id, 하행역_id);
    }

    @DisplayName("노선에 등록되지않은 역을 삭제할 수 없다.")
    @Test
    void doesNotDeleteSection() {

        //given
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);
        long 추가_하행역_id = 지하철역_생성_요청_후_id_추출(또다른지하철역이름);
        지하철_노선_구간_추가_등록(지하철역_노선_id, 추가_하행역_id, 하행역_id, 구간거리);

        //when
        long 없는역_id = -1;

        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(지하철역_노선_id, 없는역_id);

        //then
        API_잘못된요청_응답코드_검사(response);
        삭제할_노선_구간이_없는역일경우_에러(response);

    }

    @DisplayName("지하철 구간 삭제 시  구간이 1개인 경우 역을 삭제할 수 없다.")
    @Test
    void doesNotDeleteSectionWhenSectionOnlyOne() {

        //given
        long 상행역_id = 지하철역_생성_요청_후_id_추출(지하철역이름);
        long 하행역_id = 지하철역_생성_요청_후_id_추출(새로운지하철역이름);
        long 지하철역_노선_id = 지하철역_노선_등록_요청_후_id_추출(신분당선, red, 상행역_id, 하행역_id, distance);

        //when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(지하철역_노선_id, 하행역_id);

        //then
        API_잘못된요청_응답코드_검사(response);
        삭제할_노선_구간_1개인경우_에러(response);

    }
}
