package nextstep.subway.exception.code;

import lombok.Getter;

@Getter
public enum StationCode implements ResponseCode{
    STATION_NOT_FOUND(3000, "지하철역이 존재하지 않습니다.");

    private final int code;

    private final String message;

    StationCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
