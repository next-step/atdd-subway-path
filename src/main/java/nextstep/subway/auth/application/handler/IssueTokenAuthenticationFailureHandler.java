package nextstep.subway.auth.application.handler;

import nextstep.subway.auth.ui.interceptor.authentication.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IssueTokenAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Vary", "Origin");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
