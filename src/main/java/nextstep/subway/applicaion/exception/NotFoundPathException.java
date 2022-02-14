package nextstep.subway.applicaion.exception;

public class NotFoundPathException extends RuntimeException {
	final static String message = "해당 경로로는 갈 수 없습니다. - [%s -> %s]";

	public NotFoundPathException(String departName, String arrivalName) {
		super(String.format(message, departName, arrivalName));
	}
}
