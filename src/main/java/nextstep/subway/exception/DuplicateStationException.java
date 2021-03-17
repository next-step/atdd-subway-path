package nextstep.subway.exception;

public class DuplicateStationException extends RuntimeException {
    public DuplicateStationException() {
        super("해당 라인에 이미 등록된 역입니다.");
    }
}