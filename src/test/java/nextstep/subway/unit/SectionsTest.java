package nextstep.subway.unit;

import static nextstep.subway.utils.StationFixture.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionDuplicationStationException;
import nextstep.subway.exception.SectionLongDistanceException;
import nextstep.subway.exception.SectionNotIncludedException;

public class SectionsTest {

	private Sections sections;
	private Line 신분당선;
	private Station 신사역;
	private Station 논현역;
	private Station 신논현역;

	@BeforeEach
	void setUp() {
		신분당선 = new Line(1L, "신분당선", "red");
		신사역 = new Station(1L, "신사역");
		논현역 = new Station(2L, "논현역");
		신논현역 = new Station(3L, "신논현역");
		sections = new Sections();
		sections.addSection(new Section(신분당선, 신사역, 논현역, 10));
	}

	@DisplayName("구간을 추가할 때 새로운 역을 역 사이에 추가할 경우 기존 역 사이 길이보다 길거나 같으면 에러를 반환한다.")
	@Test
	void addSectionFail() {
		// given

		// when

		// then
		Assertions.assertThrows(SectionLongDistanceException.class,
			() -> sections.addSection(new Section(신분당선, 신논현역, 논현역, 10)));
	}

	@DisplayName("구간을 추가할 때 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 에러를 반환한다.")
	@Test
	void addSectionFail2() {
		// given

		// when

		// then
		Assertions.assertThrows(SectionDuplicationStationException.class,
			() -> sections.addSection(new Section(신분당선, 신사역, 논현역, 5)));
	}

	@DisplayName("구간을 추가할 때 상행역과 하행역이 둘 다 포함되어 있지 않다면 에러를 반환한다.")
	@Test
	void addSectionFail3() {
		// given

		// when

		// then
		Assertions.assertThrows(SectionNotIncludedException.class,
			() -> sections.addSection(new Section(신분당선, new Station(강남역), new Station(양재역), 5)));
	}
}
