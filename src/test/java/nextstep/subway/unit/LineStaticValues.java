package nextstep.subway.unit;

import nextstep.subway.domain.Station;

public class LineStaticValues {
	static final String LINE3_NAME = "3호선";
	static final String LINE3_COLOR = "Orange";
	static final Long 강남역Id = 1L;
	static final Long 역삼역Id = 2L;
	static final Long 선릉역Id = 3L;
	static final Long 양재역Id = 4L;
	static final Long 정자역Id = 5L;
	static final Long 양재시민의숲역Id = 6L;
	static final Long 신논현역Id = 7L;
	static final Long 미금역Id = 8L;
	static Station 강남역 = new Station(강남역Id, "강남역");
	static Station 역삼역 = new Station(역삼역Id, "역삼역");
	static Station 선릉역 = new Station(선릉역Id, "선릉역");
	static Station 양재역 = new Station(양재역Id, "양재역");
	static Station 정자역 = new Station(정자역Id, "정자역");
	static Station 양재시민의숲역 = new Station(양재시민의숲역Id, "양재시민의숲역");
	static Station 신논현역 = new Station(신논현역Id, "신논현역");
	static Station 미금역 = new Station(미금역Id, "미금역");

	static int DISTANCE = 9;
}
