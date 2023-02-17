package nextstep.subway.ui.error.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {

	//station
	MISMATCHED_THIS_DOWN_STATION(BAD_REQUEST, "해당 노선에 등록되어있는 하행 종점역과 같지 않습니다."),
	MISMATCHED_STATION(BAD_REQUEST, "해당 노선에 이미 등록되어있는 역입니다."),
	STATION_NOT_FINAL(BAD_REQUEST, "마지막 구간이 아닙니다"),
	STATION_LESS_THAN_TWO(BAD_REQUEST, "노선에 역이 두개 이하입니다."),
	STATION_NOT_EXISTS(BAD_REQUEST, "해당 역은 존재하지 않습니다."),
	ALREADY_REGISTERED_STAION(BAD_REQUEST, "이미 등록된 역입니다."),
	//line
	LINE_NOT_EXISTS(BAD_REQUEST, "해당 노선은 존재하지 않습니다."),

	//section
	SECTION_NOT_EXISTS(BAD_REQUEST, "해당 구간은 존재하지 않습니다."),
	SECTION_NOT_LONGER_THEN_EXISTING_SECTION(BAD_REQUEST, "역 사이에 기존 역 사이 길이보다 크거나 같은 구간을 등록할 수 없습니다."),
	SECTION_NOT_DELETE_THEN_ONE(BAD_REQUEST, "구간이 하나 이하일때 제거할 수 없습니다."),

	//path
	SAME_SOURCE_AND_TARGET(BAD_REQUEST, "출발지와 도착지가 같습니다.");


	private HttpStatus status;
	private String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
