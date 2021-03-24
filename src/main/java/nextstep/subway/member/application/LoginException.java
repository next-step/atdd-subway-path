package nextstep.subway.member.application;

public class LoginException extends RuntimeException {
    public LoginException() {
        super("이메일과 비밀번호를 확인해주세요.");
    }
}
