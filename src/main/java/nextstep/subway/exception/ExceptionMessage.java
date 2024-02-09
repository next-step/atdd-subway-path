package nextstep.subway.exception;

import lombok.Getter;

@Getter
public enum ExceptionMessage {
//    UPSTATION_VALIDATION_EXCEPTION("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다."),
//    DOWNSTATION_VALIDATION_EXCEPTION("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다."),
    NEW_SECTION_VALIDATION_EXCEPTION("새로운 구간의 상행역과 하행역은 같을 수 없습니다."),
    DELETE_LAST_SECTION_EXCEPTION("마지막 종착역만 제거할 수 있습니다."),
    DELETE_ONLY_ONE_SECTION_EXCEPTION("지하철 노선에 구간이 1개인 경우 역을 삭제할 수 없다."),
    ALREADY_REGISTERED_SECTION_EXCEPTION("이미 등록된 구간은 등록할 수 없습니다."),
    NO_EXISTS_SAME_UPSTATION_SECTION("동일한 상행역을 가진 구간이 없습니다."),
    NO_EXISTS_SAME_DOWNSTATION_SECTION("동일한 하행역을 가진 구간이 없습니다.");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }
}
