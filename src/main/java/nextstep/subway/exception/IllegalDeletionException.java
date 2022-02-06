package nextstep.subway.exception;

public class IllegalDeletionException extends RuntimeException {
  public IllegalDeletionException() {
    super(Messages.ILLEGAL_DELETION.message());
  }
}
