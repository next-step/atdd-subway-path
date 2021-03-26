package nextstep.subway.auth.ui.interceptor.authentication;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("인증에 실패하였습니다.");
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
