package nextstep.subway.line.exception;

public class DownStationExistedException extends RuntimeException {

    public DownStationExistedException() {
        super("하행역이 이미 등록되어 있습니다.");
    }
}
