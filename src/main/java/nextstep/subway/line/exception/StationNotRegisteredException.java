package nextstep.subway.line.exception;

public class StationNotRegisteredException extends RuntimeException {

    public StationNotRegisteredException() {
        super("지하철 노선에 등록되어 있지 않은 역입니다.");
    }
}
