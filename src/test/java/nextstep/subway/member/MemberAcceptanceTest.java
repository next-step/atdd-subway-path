package nextstep.subway.member;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.TestConstants.*;
import static nextstep.subway.member.MemberSteps.*;

@DisplayName("회원 관련 기능")
public class MemberAcceptanceTest extends AcceptanceTest {

    @DisplayName("중복 이메일로 회원가입을 한다.")
    @Test
    void createMemberWithSameEmail() {
        회원_생성_요청(OTHER_EMAIL, OTHER_PASSWORD, NAME);

        ExtractableResponse<Response> response = 회원_생성_요청(OTHER_EMAIL, OTHER_PASSWORD, NAME);
        // then
        회원_생성_실패됨(response);
    }

    @DisplayName("중복 이메일 여부를 확인 한다.")
    @Test
    void validateEmailFail() {
        // given
        회원_생성_요청(OTHER_EMAIL, OTHER_PASSWORD, NAME);

        // when
        ExtractableResponse<Response> response = 이메일_존재_여부_확인_요청(OTHER_EMAIL);

        // then
        이메일_존재함(response);
    }

    @DisplayName("중복 이메일 여부를 확인 한다.")
    @Test
    void validateEmailSuccess() {
        // when
        ExtractableResponse<Response> response = 이메일_존재_여부_확인_요청(OTHER_EMAIL);

        // then
        이메일_사용가능함(response);
    }

    @DisplayName("나의 정보를 관리한다.")
    @Test
    void manageMyInfo() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성_요청(OTHER_EMAIL, OTHER_PASSWORD, NAME);
        // then
        회원_생성됨(createResponse);

        TokenResponse tokenResponse = 로그인_되어_있음(OTHER_EMAIL, OTHER_PASSWORD);

        // when
        ExtractableResponse<Response> findResponse = 내_회원_정보_조회_요청(tokenResponse);
        // then
        회원_정보_조회됨(findResponse, OTHER_EMAIL, NAME);

        // when
        ExtractableResponse<Response> updateResponse = 내_회원_정보_수정_요청(tokenResponse, "new" + OTHER_EMAIL, "new" + OTHER_PASSWORD, NAME + 2);
        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 내_회원_삭제_요청(tokenResponse);
        // then
        회원_삭제됨(deleteResponse);
    }
}