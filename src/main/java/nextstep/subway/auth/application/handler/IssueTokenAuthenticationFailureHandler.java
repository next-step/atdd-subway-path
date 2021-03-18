package nextstep.subway.auth.application.handler;

import nextstep.subway.auth.ui.interceptor.authentication.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IssueTokenAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Vary", "Origin");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
