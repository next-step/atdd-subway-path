package nextstep.subway.exception.code;

import lombok.Getter;

@Getter
public enum CommonCode implements ResponseCode {
    ETC(1000, "알 수 없는 오류입니다."),
    PARAM_INVALID(1001, "올바르지 않은 파라미터입니다.");

    private final int code;

    private final String message;

    CommonCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
