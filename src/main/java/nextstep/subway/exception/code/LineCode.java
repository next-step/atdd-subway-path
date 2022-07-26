package nextstep.subway.exception.code;

import lombok.Getter;

@Getter
public enum LineCode implements ResponseCode{
    LINE_NOT_FOUND(2000, "노선이 존재하지 않습니다."),
    LINE_STATION_DUPLICATE(2001, "노선에 이미 존재하는 역입니다.");

    private final int code;

    private final String message;

    LineCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
