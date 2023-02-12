package nextstep.subway.domain.exception;

public class RemoveSectionsSizeException extends IllegalArgumentException {
    private static final String MESSAGE = "구간 목록의 크기가 %d 이상일 경우 구간 제거가 가능합니다.";

    public RemoveSectionsSizeException(final int size) {
        super(String.format(MESSAGE, size));
    }
}
