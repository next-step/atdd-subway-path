package nextstep.subway.exception.code;

import lombok.Getter;

@Getter
public enum SectionCode implements ResponseCode{
    SECTION_NOT_MATCH(4000, "구간 상행선과 노선 하행선과 일치하지 않습니다."),
    SECTION_REMOVE_INVALID(4001, "구간을 삭제할 수 없습니다.");

    private final int code;

    private final String message;

    SectionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
