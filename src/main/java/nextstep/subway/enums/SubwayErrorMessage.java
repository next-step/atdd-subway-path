package nextstep.subway.enums;

import com.sun.istack.localization.LocalizableMessageFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SubwayErrorMessage {
    NOT_EXIST_STATION_OF_SECTION("등록되지 않은 지하철역 입니다."),
    EXIST_SECTION( "등록된 구간 입니다."),
    INVALID_DISTANCE( "새로운 구간의 길이는 기존 구간의 길이보다 짧아야 합니다."),
    MUST_BE_REGISTERED_TWO_SECTION("두개 이상의 구간이 등록되어야 제거가 가능합니다."),
    SAME_SOURCE_AND_DESTINATION("출발지와 목적지가 동일합니다."),
    UNCONNECTED_PATH("연결되지 않은 경로 입니다.");

    private final String message;
}
