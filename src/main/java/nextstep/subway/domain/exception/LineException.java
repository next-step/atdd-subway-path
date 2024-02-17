package nextstep.subway.domain.exception;

public class LineException extends DomainException {

    public static final String ALREADY_REGISTERED_STATION_EXCEPTION = "이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.";
    public static final String NOT_EXIST_SECTION = "노선에 등록된 구간이 없습니다.";
    public static final String NOT_ADDABLE_SECTION = "노선에 등록할 수 없는 구간입니다.";
    public static final String NOT_REMOVE_EXCEPTION = "노선을 삭제할 수 없습니다.";
    public static final String NOT_EXIST_STATION = "노선에 등록되지 않은 역입니다.";

    public LineException(String message) {
        super(message);
    }

}
