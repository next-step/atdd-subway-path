package nextstep.subway.auth.ui.interceptor.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.auth.application.UserDetailsService;
import nextstep.subway.auth.application.handler.IssueTokenAuthenticationFailureHandler;
import nextstep.subway.auth.application.handler.IssueTokenSuccessHandler;
import nextstep.subway.auth.application.provider.AuthenticationManager;
import nextstep.subway.auth.application.provider.AuthenticationProvider;
import nextstep.subway.auth.domain.Authentication;
import nextstep.subway.auth.domain.AuthenticationToken;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.infrastructure.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.stream.Collectors;

public class TokenAuthenticationInterceptor extends AbstractAuthenticationInterceptor {
    private AuthenticationManager authenticationManager;

    public TokenAuthenticationInterceptor(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        super(new IssueTokenSuccessHandler(jwtTokenProvider), new IssueTokenAuthenticationFailureHandler());
        this.authenticationManager = new AuthenticationProvider(userDetailsService);
    }

    @Override
    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthenticationToken authenticationToken = convert(request);
        return authenticationManager.authenticate(authenticationToken);
    }

    public AuthenticationToken convert(HttpServletRequest request) throws IOException {
        try {
            String content = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            TokenRequest tokenRequest = new ObjectMapper().readValue(content, TokenRequest.class);

            String principal = tokenRequest.getEmail();
            String credentials = tokenRequest.getPassword();

            return new AuthenticationToken(principal, credentials);
        } catch (RuntimeException e) {
            throw new InvalidParameterException();
        }

    }
}
