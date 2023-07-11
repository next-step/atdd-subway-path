package nextstep.subway.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_EXIST_STATION("지하철역이 존재하지 않습니다."),
    NOT_EXIST_LINE("지하철 노선이 존재하지 않습니다."),

    INVALID_UP_STATION("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다."),
    ALREADY_REGISTERED_STATION("이미 지하철 노선에 등록되어 있는 역입니다."),
    IS_NOT_LAST_LINE_SECTION("지하철 노선의 마지막 구간이 아닙니다."),
    UNREGISTERED_STATION("지하철 노선에 등록되어 있지 않은 역입니다."),
    STAND_ALONE_LINE_SECTION("지하철 노선에 구간이 1개만 존재할 경우 삭제할 수 없습니다.");

    private String message;

}
