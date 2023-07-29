package nextstep.subway.unit;

import static nextstep.subway.utils.LineFixture.경강선_색상;
import static nextstep.subway.utils.LineFixture.경강선_이름;
import static nextstep.subway.utils.LineFixture.수인분당선_색상;
import static nextstep.subway.utils.LineFixture.수인분당선_이름;
import static nextstep.subway.utils.LineFixture.신분당선_색상;
import static nextstep.subway.utils.LineFixture.신분당선_이름;
import static nextstep.subway.utils.StationFixture.강남역_이름;
import static nextstep.subway.utils.StationFixture.논현역_이름;
import static nextstep.subway.utils.StationFixture.신논현역_이름;
import static nextstep.subway.utils.StationFixture.신사역_이름;
import static nextstep.subway.utils.StationFixture.양재시민의숲역_이름;
import static nextstep.subway.utils.StationFixture.양재역_이름;
import static nextstep.subway.utils.StationFixture.이매역_이름;
import static nextstep.subway.utils.StationFixture.판교역_이름;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.PathNotConnectedStationException;
import nextstep.subway.exception.PathSameStationException;
import nextstep.subway.exception.SectionNotIncludedException;

public class PathTest {

	private PathFinder pathFinder;
	private Station 신사역;
	private Station 논현역;
	private Station 신논현역;
	private Station 판교역;
	private Station 이매역;
	private Station 강남역;
	private Station 양재역;

	/**
	 *  논현역 --- 신분당선 --- 신논현역
	 *    |                     |
	 * 신분당선                경강선
	 *    |                     |
	 *  신사역                 판교역 --- 경강선 --- 이매역
	 *
	 * 강남역 --- 수인분당선 --- 양재역
	 */
	@BeforeEach
	void init() {
		신사역 = new Station(1L, 신사역_이름);
		논현역 = new Station(2L, 논현역_이름);
		신논현역 = new Station(3L, 신논현역_이름);
		판교역 = new Station(4L, 판교역_이름);
		이매역 = new Station(5L, 이매역_이름);
		강남역 = new Station(6L, 강남역_이름);
		양재역 = new Station(7L, 양재역_이름);

		Line 신분당선 = new Line(신분당선_이름, 신분당선_색상);
		Line 경강선 = new Line(경강선_이름, 경강선_색상);
		Line 수인분당선 = new Line(수인분당선_이름, 수인분당선_색상);

		신분당선.addSection(신사역, 논현역, 10);
		신분당선.addSection(논현역, 신논현역, 5);
		경강선.addSection(신논현역, 판교역, 7);
		경강선.addSection(판교역, 이매역, 10);
		수인분당선.addSection(강남역, 양재역, 5);

		pathFinder = new PathFinder(List.of(신분당선, 경강선, 수인분당선));
	}

	@Test
	void 지하철_경로를_조회한다() {
		// when
		Path actual = pathFinder.findPath(논현역, 판교역);

		// then
		assertThat(actual.getStations()).containsExactly(논현역, 신논현역, 판교역);
		assertThat(actual.getTotalDistance()).isEqualTo(12);
	}

	@Test
	void 지하철_경로_조회_시_출발역과_도착역이_같을_경우_에러를_반환한다() {
		// then
		Assertions.assertThrows(PathSameStationException.class,
			() -> pathFinder.findPath(신사역, 신사역));
	}

	@Test
	void 지하철_경로_조회_시_노선에_등록되지_않은_역이면_에러를_반환한다() {
		// given
		Station lineNotIncludedStation = new Station(8L, 양재시민의숲역_이름);

		// then
		Assertions.assertThrows(SectionNotIncludedException.class,
			() -> pathFinder.findPath(lineNotIncludedStation, 판교역));
	}

	@Test
	void 지하철_경로_조회_시_출발역과_도착역이_연결되어_있지_않으면_에러를_반환한다() {
		// given

		// then
		Assertions.assertThrows(PathNotConnectedStationException.class,
			() -> pathFinder.findPath(신사역, 양재역));
	}
}
