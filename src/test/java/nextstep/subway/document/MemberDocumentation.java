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
                .body(tokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value()).extract();

        TokenRequest failTokenRequest = new TokenRequest(OTHER_EMAIL, OTHER_PASSWORD + 1);
        RestAssured
                .given(spec).log().all()
                .filter(document("member/login-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(failTokenRequest)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value()).extract();
    }
}
