package nextstep.subway.exception;

public enum ExceptionMessage {
	// Station
	ALREADY_EXIST_STATIONS("역들이 이미 노선에 포함되어 있습니다."),
	DOES_NOT_EXIST_STATIONS("역들이 노선에 존재하지 않습니다."),

	// Section
	CANNOT_DELETE_SECTION("구간을 삭제할 수 없습니다."),
	ONLY_ONE_SECTION("한 개의 구간만이 존재합니다."),
	TOO_LONG_DISTANCE_OF_SECTION("추가될 구간의 거리가 너무 큽니다.");

	private String msg;

	ExceptionMessage(String msg) {
		this.msg = msg;
	}

	public String msg() {
		return msg;
	}
}
