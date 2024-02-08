package nextstep.subway.common.exception;

public class CustomException {
    public static class Conflict extends RuntimeException {

        public Conflict(String message) {
            super(message);
        }
    }
}
