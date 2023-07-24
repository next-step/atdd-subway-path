package nextstep.subway.unit;

import static nextstep.subway.utils.LineFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

class LineTest {

	private Station 신사역;
	private Station 논현역;
	private Station 신논현역;

	@BeforeEach
	void init() {
		신사역 = new Station(1L, "신사역");
		논현역 = new Station(2L, "논현역");
		신논현역 = new Station(3L, "신논현역");
	}

	@DisplayName("지하철 노선에 구간을 추가한다.")
	@Test
	void addSection() {
		// given
		Line line = createLineAndAddSection();

		// when
		line.addSection(논현역, 신논현역, 10);

		// then
		assertThat(line.getStations()).contains(신사역, 논현역, 신논현역);
	}

	@DisplayName("지하철 노선에 등록된 지하철역들을 반환한다.")
	@Test
	void getStations() {
		// given
		Line line = createLineAndAddSection();

		// when
		List<Station> actual = line.getStations();

		// then
		assertThat(actual).contains(신사역, 논현역);
	}

	@DisplayName("지하철 노선에 해당 구간을 삭제한다.")
	@Test
	void removeSection() {
		// given
		Line line = createLineAndAddSection();
		line.addSection(논현역, 신논현역, 15);

		// when
		line.removeSection(신논현역.getId());

		// then
		assertThat(line.getStations()).contains(신사역, 논현역);
	}

	private Line createLineAndAddSection() {
		Line line = new Line(1L, 신분당선_이름, 신분당선_이름);
		line.addSection(신사역, 논현역, 10);
		return line;
	}
}
