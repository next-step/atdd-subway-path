package nextstep.subway.ui;

import lombok.Getter;

@Getter
public enum SubwayErrorCode {
	/*** invalid request parameters ***/
	INVALID_DISTANCE("The distance is too large"),
	INVALID_STATION("Station id does not match"),
	INVALID_LINE("Line id does not match"),
	SAME_STATION("source and target must not be the same"),

	/*** resource problem ***/
	EXIST_STATION("This section already exists"),
	NO_SECTIONS_LEFT("There is only one section left"),
	NOT_EXIST_STATION("Station does not registered"),
	NOT_FOUND_PATHS("Not found paths");

	private final String message;

	SubwayErrorCode(String message) {
		this.message = message;
	}
}
