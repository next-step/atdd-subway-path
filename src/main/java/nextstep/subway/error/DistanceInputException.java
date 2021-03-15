package nextstep.subway.error;

public class DistanceInputException extends RuntimeException{
    public DistanceInputException() { super("잘못된 거리를 입력하셨습니다."); }
}
