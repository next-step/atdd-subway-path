package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineUtils {

	public static Station 광교중앙역_생성() {
		return new Station("광교중앙역");
	}

	public static Station 광교역_생성() {
		return new Station("광교역");
	}

	public static Line 신분당선_생성() {
		return new Line("신분당선", "red");
	}
}
