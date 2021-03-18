package nextstep.subway.document;

import io.restassured.RestAssured;
import nextstep.subway.Documentation;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.member.dto.MemberRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.TestConstants.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class MemberDocumentation extends Documentation {
    @Test
    void member() {
        MemberRequest memberRequest = new MemberRequest(OTHER_EMAIL, OTHER_PASSWORD, NAME);

        RestAssured
                .given(spec).log().all()
                .filter(document("member/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Origin", "http://localhost:8080")
                .body(memberRequest)
                .when().post("/members")
                .then().log().all().extract();

        TokenRequest tokenRequest = new TokenRequest(OTHER_EMAIL, OTHER_PASSWORD);

        RestAssured
                .given(spec).log().all()
                .filter(document("member/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Origin", "http://localhost:8080")
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        RestAssured
                .given(spec).log().all()
                .filter(document("member/me",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .header("Authorization", "Bearer " + 로그인_사용자.getAccessToken())
                .header("Origin", "http://localhost:8080")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/members/me")
                .then().log().all().extract();

        TokenRequest failTokenRequest = new TokenRequest(OTHER_EMAIL, OTHER_PASSWORD + 1);
        RestAssured
                .given(spec).log().all()
                .filter(document("member/login-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Origin", "http://localhost:8080")
                .body(failTokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value()).extract();
    }
}
