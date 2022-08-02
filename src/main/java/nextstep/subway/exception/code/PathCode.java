package nextstep.subway.exception.code;

import lombok.Getter;

@Getter
public enum PathCode implements ResponseCode {
    NOT_LINKED(5000, "지하철역이 서로 연결되어있지 않습니다.");

    private final int code;

    private final String message;

    PathCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
