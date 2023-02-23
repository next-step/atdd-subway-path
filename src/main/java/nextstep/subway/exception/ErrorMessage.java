package nextstep.subway.exception;

public enum ErrorMessage {
	LINE_NOT_FOUND("지하철 호선을 찾을 수 없습니다."),
	STATION_NOT_FOUND("지하철역을 찾을 수 없습니다"),
	SHOULD_EXIST_NEW_STATION("새로운 역이 존재해야 합니다."),
	SHOULD_BE_INCLUDE_UP_STATION_OR_DOWN_STATION("상행역, 하행역 둘 중 하나는 포함하고 있어야 합니다."),
	SHOULD_DELETE_ONLY_DOWNSTATION_OF_LINE("마지막 구간만 제거할 수 있습니다."),
	CANNOT_DELETE_LINE_CONSIST_OF_ONE_SECTION("노선에 구간이 1개인 경우 역을 삭제할 수 없습니다."),
	DISTANCE_OF_SECTION_MUST_BE_POSITIVE("구간의 거리는 양수이어야 합니다."),
	CANNOT_FIND_FINAL_DOWN_STATION("하행 종점역을 찾을 수 없습니다."),
	CANNOT_FIND_FINAL_UP_STATION("상행 종점역을 찾을 수 없습니다."),
	CANNOT_FIND_SECTION("구간을 찾을 수 없습니다."),
	CANNOT_REMOVE_NO_LAST_DOWN_STATION("마지막 역이 아니면 삭제할 수 없습니다.");

	private final String message;

	ErrorMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
