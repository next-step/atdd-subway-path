package nextstep.subway.unit;

import nextstep.subway.domain.Station;

public class LineStaticValues {
	static final String LINE3_NAME = "3호선";
	static final String LINE3_COLOR = "Orange";
	static final Long 강남역Id = 1L;
	static final Long 역삼역Id = 2L;
	static final Long 선릉역Id = 3L;
	static Station 강남역 = new Station(강남역Id, "강남역");
	static Station 역삼역 = new Station(역삼역Id, "역삼역");
	static Station 선릉역 = new Station(선릉역Id, "선릉역");
	static int DISTANCE = 9;
}
