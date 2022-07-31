package nextstep.subway.exception;

public class SubwayException extends RuntimeException {
	public SubwayException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage.msg());
	}
}
