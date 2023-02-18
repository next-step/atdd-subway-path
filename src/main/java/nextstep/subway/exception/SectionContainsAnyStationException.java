package nextstep.subway.exception;

public class SectionContainsAnyStationException extends RuntimeException {
    private static String message = "두 역이 어느 구간에도 속해있지 않습니다.";

    public SectionContainsAnyStationException() {
        super(message);
    }
}
