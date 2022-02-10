package nextstep.subway.exception;

import org.springframework.http.HttpStatus;

public class SubwayException {

	public static class DuplicatedException extends BusinessException {
		private static final String DEFAULT_ERROR_MESSAGE = "Duplicate";

		public DuplicatedException() {
			super(DEFAULT_ERROR_MESSAGE, HttpStatus.BAD_REQUEST.value());
		}

		public DuplicatedException(String message) {
			super(message, HttpStatus.BAD_REQUEST.value());
		}
	}

	public static class WrongParameterException extends BusinessException {
		private static final String DEFAULT_ERROR_MESSAGE = "Duplicate";

		public WrongParameterException() {
			super(DEFAULT_ERROR_MESSAGE, HttpStatus.BAD_REQUEST.value());
		}

		public WrongParameterException(String message){
			super(message, HttpStatus.BAD_REQUEST.value());
		}
	}

	public static class NotFoundException extends BusinessException {
		private static final String DEFAULT_ERROR_MESSAGE = "Can not found";

		public NotFoundException() {
			super(DEFAULT_ERROR_MESSAGE, HttpStatus.NO_CONTENT.value());
		}

		public NotFoundException(String message) {
			super(message, HttpStatus.NO_CONTENT.value());
		}
	}

	public static class CanNotDeleteException extends BusinessException {
		private static final String DEFAULT_ERROR_MESSAGE = "Can not Delete";

		public CanNotDeleteException() {
			super(DEFAULT_ERROR_MESSAGE, HttpStatus.CONFLICT.value());
		}

		public CanNotDeleteException(String message) {
			super(message, HttpStatus.CONFLICT.value());
		}
	}
}
