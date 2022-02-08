package nextstep.subway.exception;

public class NotExistedStationDeleteException extends RuntimeException {
    private static final String MESSAGE = "삭제하고자 하는 역이 노선에 존재하지 않으면 삭제가 불가능함";

    public NotExistedStationDeleteException() {
        super(MESSAGE);
    }
}

