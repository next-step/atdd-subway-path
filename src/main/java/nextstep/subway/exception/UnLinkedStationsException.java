package nextstep.subway.exception;

public class UnLinkedStationsException extends RuntimeException {
  public UnLinkedStationsException() {
    super(Messages.UNLINKED_STATIONS.message());
  }
}
