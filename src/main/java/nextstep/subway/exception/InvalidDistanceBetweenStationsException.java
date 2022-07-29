package nextstep.subway.exception;

public class InvalidDistanceBetweenStationsException extends RuntimeException {

    private static final String INVALID_DISTANCE_VALUE_EXCEPTION = "역사이에 구간을 추가하는 경우 반드시 기존 역사이의 거리보다 작아야 합니다";

    public InvalidDistanceBetweenStationsException() {
        super(INVALID_DISTANCE_VALUE_EXCEPTION);
    }

}