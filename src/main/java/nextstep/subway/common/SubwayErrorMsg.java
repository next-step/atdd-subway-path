package nextstep.subway.common;

public enum SubwayErrorMsg {
	NOT_EXIST_LINE("Line doesn't exist"),
	NOT_EXIST_STATION("Station doesn't exist"),

	SECTIONS_EXIST_ALL_STATION("등록하려는 구간의 상행역과 하행역이 이미 노선에 등록되어 있습니다."),
	SECTIONS_NOT_EXIST_STATION("등록하려는 구간의 상행역과 하행역이 노선에 등록되어 있지 않습니다."),
	SECTIONS_DISTANCE_LENGTH("등록하려는 구간의 길이가 기존 구간의 길이보다 크거나 같습니다."),
	SECTIONS_LAST_SECTION("삭제하려는 구간이 노선의 마지막 구간입니다."),

	PATH_SAME_STATION("출발역과 도착역이 같습니다."),
	PATH_NO_CONNECT("출발역과 도착역이 연결되지 않았습니다."),
	;

	private final String message;

	SubwayErrorMsg(String message) {
		this.message = message;
	}

	public String isMessage() {
		return message;
	}
}
