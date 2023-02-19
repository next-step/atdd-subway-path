package nextstep.subway.domain.exception.path;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PathErrorMessage {
    INVALID_PATH("잘못된 경로요청입니다."),
    SOURCE_AND_TARGET_CANNOT_BE_SAME("출발역과 도착역은 같을 수 없습니다."),
    STATION_NOT_REGISTERED("등록되지 않은 역의 경로를 조회할 수 없습니다."),

    ;


    private final String message;

}
