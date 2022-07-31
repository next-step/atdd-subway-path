package nextstep.subway.ui;

import lombok.Getter;

@Getter
public enum SubwayErrorCode {
	/*** invalid request parameters ***/
	INVALID_DISTANCE("The distance is too large"),
	INVALID_STATION("Station does not match"),

	/*** resource problem ***/
	EXIST_STATION("This section already exists"),
	NO_SECTIONS_LEFT("There is only one section left"),
	NOT_EXIST_STATION("Station does not registered");



	private final String message;

	SubwayErrorCode(String message) {
		this.message = message;
	}
}
