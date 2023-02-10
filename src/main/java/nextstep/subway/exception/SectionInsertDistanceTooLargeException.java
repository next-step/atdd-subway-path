package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SectionInsertDistanceTooLargeException extends SubwayException {
    public static final String MESSAGE = "중간에 삽입되는 구간은 기존 구간 길이보다 크거나 같을 수 없습니다.";

    public SectionInsertDistanceTooLargeException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
