package nextstep.subway.section;

public class SectionNotFoundException extends RuntimeException{
  public SectionNotFoundException(Long id) {
    super(String.format("id: %d Section is not existed", id));
  }
}
