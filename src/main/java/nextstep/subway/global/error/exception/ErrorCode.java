package nextstep.subway.global.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NEW_SECTION_LENGTH_MUST_BE_SMALLER_THAN_EXISTING_SECTION_LENGTH(HttpStatus.BAD_REQUEST.value(), "추가하는 구간이 기존의 구간의 역 사이의 길이보다 크거나 같으면 등록을 할 수 없습니다"),
    NOT_STATION_OF_END_SECTION(HttpStatus.BAD_REQUEST.value(), "노선의 마지막 구간의 하행선이 아닙니다.");

    private int status;
    private String errorMessage;

    ErrorCode(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
