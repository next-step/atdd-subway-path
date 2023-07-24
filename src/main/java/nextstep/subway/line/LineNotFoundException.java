package nextstep.subway.line;

public class LineNotFoundException extends RuntimeException{
  public LineNotFoundException(Long id) {
    super(String.format("id: %d Line is not existed", id));
  }
}
