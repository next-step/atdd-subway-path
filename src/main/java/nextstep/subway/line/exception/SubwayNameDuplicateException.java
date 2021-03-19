package nextstep.subway.line.exception;

public class SubwayNameDuplicateException extends RuntimeException {
  public SubwayNameDuplicateException() {
    super("이미 등록된 노선 이름입니다.");
  }
}
