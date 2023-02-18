package nextstep.subway.domain.exception.line;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LineErrorMessage {
    INVALID_DISTANCE("새로운 구간의 길이는 기존 구간의 길이보다 같거나 길 수 없습니다."),
    LINE_HAS_BOTH_STATION("새로운 구간의 상행역과 하행역 모두 등록된 역일 수 없습니다."),
    NEW_SECTION_COULD_HAVE_ANY_REGISTERED_STATION("새로운 구간의 상행역 또는 하행역 둘 중 하나는 노선에 등록된 역이어야 합니다."),
    SECTION_NOT_REGISTERED("해당 노선에는 구간이 등록되어있지 않습니다."),
    LINE_HAS_NOT_ENOUGH_SECTION("구간이 한 개이하인 노선을 삭제할 수 없습니다."),
    INVALID_REMOVE_STATION("노선에 존재하지 않는 역입니다."),
    ;

    private final String message;

}
