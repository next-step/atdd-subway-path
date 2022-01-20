package nextstep.subway.line.exception;

public class DownStationExistedException extends LineDomainException{
    private static String message = "하행역이 이미 등록되어 있습니다.";

    public DownStationExistedException() {
        super(message);
    }
}
