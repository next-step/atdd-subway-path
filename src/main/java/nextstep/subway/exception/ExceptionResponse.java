package nextstep.subway.exception;


public class ExceptionResponse {
	private int code;
	private String msg;

	public ExceptionResponse() {
	}

	public ExceptionResponse(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
