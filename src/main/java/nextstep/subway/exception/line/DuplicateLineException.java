package nextstep.subway.exception.line;

public class DuplicateLineException extends RuntimeException {

    private static final String MESSAGE = "노선 이름이 중복 됩니다. - %s";

    public DuplicateLineException(String lineName) {
        super(String.format(MESSAGE, lineName));
    }

}
