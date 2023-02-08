package nextstep.subway.common;

import static nextstep.subway.common.StationFixtures.*;

import nextstep.subway.domain.Line;

public class LineFixtures {

	public static Line LINE_4() throws Exception {
		return new Line(
			"4호선",
			"blue",
			withId(동대문, 동대문_ID),
			withId(동대문역사문화공원, 동대문역사문화공원_ID),
			10
		);
	}

	public static final Long LINE_4_ID = 1L;
}
