package nextstep.subway.member.application;

import nextstep.subway.common.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
