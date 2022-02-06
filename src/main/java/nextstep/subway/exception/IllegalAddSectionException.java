package nextstep.subway.exception;

import static nextstep.subway.exception.Messages.ILLEGAL_ADD_SECTION;

public class IllegalAddSectionException extends RuntimeException {
  public IllegalAddSectionException() {
    super(ILLEGAL_ADD_SECTION.message());
  }

  public IllegalAddSectionException(String message) {
    super(message);
  }
}
