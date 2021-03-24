package nextstep.subway.member.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.subway.auth.domain.Authentication;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.auth.infrastructure.JwtTokenProvider;
import nextstep.subway.member.domain.LoginMember;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetailsService customUserDetailsService;

    public LoginService(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    public TokenResponse login(TokenRequest request) {
        LoginMember loginMember = customUserDetailsService.loadUserByUsername(request.getEmail());
        if (!loginMember.checkPassword(request.getPassword())) {
            throw new LoginException();
        }

        try {
            String payload = new ObjectMapper().writeValueAsString(new Authentication(loginMember).getPrincipal());
            String token = jwtTokenProvider.createToken(payload);
            return new TokenResponse(token);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("토큰 생성을 실패하였습니다.");
        }
    }
}

