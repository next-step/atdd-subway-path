package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionMergeStationIsNotEqualException extends SubwayException {
    public static final String MESSAGE = "합치려는 구간의 중간역이 일치하지 않습니다.";

    public SectionMergeStationIsNotEqualException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
