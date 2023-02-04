package nextstep.subway.exception;

public class CustomException extends RuntimeException{
    public static String ONLY_CAN_REMOVE_LAST_STATION_MSG = "노선의 마지막 구간만 제거할 수 있습니다.";
    public static String ONLY_CAN_CREATE_LAST_STATION_MSG = "노선의 마지막 구간에만 추가할 수 있습니다.";
    public static String DUPLICATE_STATION_MSG = "노선에 중복된 역이 존재합니다.";

    public CustomException(String message) {
        super(message);
    }
}
