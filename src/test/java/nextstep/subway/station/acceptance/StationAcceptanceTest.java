package nextstep.subway.station.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.TestConstants.OTHER_EMAIL;
import static nextstep.subway.TestConstants.OTHER_PASSWORD;
import static nextstep.subway.member.MemberSteps.로그인_되어_있음;
import static nextstep.subway.member.MemberSteps.회원_생성_요청;
import static nextstep.subway.station.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(로그인_사용자, 강남역);

        // then
        지하철역_생성됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철역_등록되어_있음(로그인_사용자, 강남역);
        ExtractableResponse<Response> createResponse2 = 지하철역_등록되어_있음(로그인_사용자, 역삼역);

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청(로그인_사용자);

        // then
        지하철역_목록_응답됨(response);
        지하철역_목록_포함됨(response, Arrays.asList(createResponse1, createResponse2));
    }

    @DisplayName("자신이 생성한 지하철역만 조회한다.")
    @Test
    void getStationsOnlyMine() {
        // given
        회원_생성_요청(OTHER_EMAIL, OTHER_PASSWORD, "사용자");
        TokenResponse 다른_로그인_사용자 = 로그인_되어_있음(OTHER_EMAIL, OTHER_PASSWORD);
        ExtractableResponse<Response> createResponse1 = 지하철역_등록되어_있음(로그인_사용자, 강남역);
        지하철역_등록되어_있음(다른_로그인_사용자, 역삼역);

        // when
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청(로그인_사용자);

        // then
        지하철역_목록_응답됨(response);
        지하철역_목록_포함됨(response, Arrays.asList(createResponse1));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_등록되어_있음(로그인_사용자, 강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_수정_요청(로그인_사용자, createResponse, new StationRequest("역삼역"));

        // then
        지하철역_수정됨(response);
    }

    @DisplayName("다른 사람이 지하철 노선을 수정한다.")
    @Test
    void updateLineFromOther() {
        // given
        회원_생성_요청(OTHER_EMAIL, OTHER_PASSWORD, "사용자");
        TokenResponse 다른_로그인_사용자 = 로그인_되어_있음(OTHER_EMAIL, OTHER_PASSWORD);
        ExtractableResponse<Response> createResponse = 지하철역_등록되어_있음(로그인_사용자, 강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_수정_요청(다른_로그인_사용자, createResponse, new StationRequest("역삼역"));

        // then
        지하철역_수정_실패됨(response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_등록되어_있음(로그인_사용자, 강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_제거_요청(로그인_사용자, createResponse);

        // then
        지하철역_삭제됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        //given
        지하철역_등록되어_있음(로그인_사용자, 강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(로그인_사용자, 강남역);

        // then
        지하철역_생성_실패됨(response);
    }

    public void 지하철역_생성됨(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public void 지하철역_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public void 지하철역_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public void 지하철역_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public void 지하철역_수정_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    public void 지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public void 지하철역_목록_포함됨(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedLineIds = createdResponses.stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }
}
