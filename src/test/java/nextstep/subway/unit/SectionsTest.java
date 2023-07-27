package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionDuplicationStationException;
import nextstep.subway.exception.SectionLastRemoveException;
import nextstep.subway.exception.SectionLongDistanceException;
import nextstep.subway.exception.SectionNotIncludedException;

public class SectionsTest {

	private Sections sections;
	private Line 신분당선;
	private Station 신사역;
	private Station 논현역;
	private Station 신논현역;
	private Station 강남역;
	private Station 양재역;

	@BeforeEach
	void setUp() {
		신분당선 = new Line(1L, "신분당선", "red");
		신사역 = new Station(1L, "신사역");
		논현역 = new Station(2L, "논현역");
		신논현역 = new Station(3L, "신논현역");
		강남역 = new Station(4L, "강남역");
		양재역 = new Station(5L, "양재역");
		sections = new Sections();
		sections.addSection(new Section(신분당선, 신사역, 논현역, 10));
	}

	@Test
	void 지하철_노선에_새로운_역을_기존_역_사이로_구간을_추가한다() {
		// given
		Section section = new Section(신분당선, 신사역, 신논현역, 5);

		// when
		sections.addSection(section);

		// then
		assertThat(sections.getStations()).containsExactly(신사역, 신논현역, 논현역);
	}

	@Test
	void 지하철_노선에_새로운_역을_상행_종점으로_구간을_추가한다() {
		// given
		Section section = new Section(신분당선, 신논현역, 신사역, 5);

		// when
		sections.addSection(section);

		// then
		assertThat(sections.getStations()).containsExactly(신논현역, 신사역, 논현역);
	}

	@Test
	void 지하철_노선에_새로운_역을_하행_종점으로_구간을_추가한다() {
		// given
		Section section = new Section(신분당선, 논현역, 신논현역, 5);

		// when
		sections.addSection(section);

		// then
		assertThat(sections.getStations()).containsExactly(신사역, 논현역, 신논현역);
	}

	@Test
	void 구간을_추가할_때_새로운_역을_역_사이에_추가할_경우_기존_역_사이_길이보다_길거나_같으면_에러를_반환한다() {
		// then
		Assertions.assertThrows(SectionLongDistanceException.class,
			() -> sections.addSection(new Section(신분당선, 신논현역, 논현역, 10)));
	}

	@Test
	void 구간을_추가할_때_상행역과_하행역이_이미_노선에_모두_등록되어_있다면_에러를_반환한다() {
		// then
		Assertions.assertThrows(SectionDuplicationStationException.class,
			() -> sections.addSection(new Section(신분당선, 신사역, 논현역, 5)));
	}

	@Test
	void 구간을_추가할_때_상행역과_하행역이_둘_다_포함되어_있지_않다면_에러를_반환한다() {
		// then
		Assertions.assertThrows(SectionNotIncludedException.class,
			() -> sections.addSection(new Section(신분당선, 강남역, 양재역, 5)));
	}

	@Test
	void 지하철_노선에_중간_구간을_삭제한다() {
		// given
		sections.addSection(new Section(신분당선, 논현역, 신논현역, 10));

		// when
		sections.removeSection(논현역.getId());

		// then
		assertThat(sections.getStations()).containsExactly(신사역, 신논현역);
	}

	@Test
	void 지하철_노선에_상행_종점역인_구간을_삭제한다() {
		// given
		sections.addSection(new Section(신분당선, 논현역, 신논현역, 10));

		// when
		sections.removeSection(신사역.getId());

		// then
		assertThat(sections.getStations()).containsExactly(논현역, 신논현역);
	}

	@Test
	void 지하철_노선에_하행_종점역인_구간을_삭제한다() {
		// given
		sections.addSection(new Section(신분당선, 논현역, 신논현역, 10));

		// when
		sections.removeSection(신논현역.getId());

		// then
		assertThat(sections.getStations()).containsExactly(신사역, 논현역);
	}

	@Test
	void 구간을_삭제할_때_마지막_구간이면_에러를_반환한다() {
		// then
		Assertions.assertThrows(SectionLastRemoveException.class,
			() -> sections.removeSection(논현역.getId()));
	}

	@Test
	void 구간을_삭제할_때_노선에_등록_되어있지_않은_역이면_에러를_반환한다() {
		// given
		sections.addSection(new Section(신분당선, 논현역, 신논현역, 10));

		// then
		Assertions.assertThrows(SectionNotIncludedException.class,
			() -> sections.removeSection(강남역.getId()));
	}
}
