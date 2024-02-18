package nextstep.subway.utils.subway;

import static nextstep.subway.utils.subway.StationSteps.역_생성_요청;

public class StationFixture {
	public static final Long 종로3가역 = 역_생성_요청("종로3가역").jsonPath().getLong("id");
	public static final Long 시청역 = 역_생성_요청("시청역").jsonPath().getLong("id");
	public static final Long 서울역 = 역_생성_요청("서울역").jsonPath().getLong("id");
	public static final Long 종각역 = 역_생성_요청("종각역").jsonPath().getLong("id");
	public static final Long 종로5가역 = 역_생성_요청("종로5가역").jsonPath().getLong("id");
	public static final Long 동대문역 = 역_생성_요청("동대문역").jsonPath().getLong("id");
}
