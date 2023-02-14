package nextstep.subway.exception;

public class IllegalSectionAddException extends RuntimeException {
    private static String message = "추가된 구간의 길이가 기존 구간의 길이보다 작아야 합니다.";

    public IllegalSectionAddException() {
        super(message);
    }
}
