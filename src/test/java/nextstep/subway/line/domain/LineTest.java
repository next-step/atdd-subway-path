package nextstep.subway.line.domain;

import nextstep.subway.common.fixture.LineFactory;
import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class LineTest {
    private static final long LINE_ID = 1L;
    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "연두색";
    private Station 강남역;
    private Station 선릉역;
    private Station 역삼역;
    private Section 강남역_선릉역_구간;
    private Section 선릉역_역삼역_구간;
    private Line line;

    @BeforeEach
    void setUp() {
        강남역 = StationFactory.createStation(1L, "강남역");
        선릉역 = StationFactory.createStation(2L, "선릉역");
        역삼역 = StationFactory.createStation(3L, "선릉역");
        강남역_선릉역_구간 = SectionFactory.createSection(1L, 강남역, 선릉역, 10);
        선릉역_역삼역_구간 = SectionFactory.createSection(2L, 선릉역, 역삼역, 20);
        line = LineFactory.createLine(LINE_ID, LINE_NAME, LINE_COLOR, 강남역_선릉역_구간);
    }

    @Test
    @DisplayName("line 을 생성할 수 있다.")
    void lineCreateTest() {
        assertSoftly(softly -> {
            softly.assertThat(line.getId()).isEqualTo(LINE_ID);
            softly.assertThat(line.getName()).isEqualTo(LINE_NAME);
            softly.assertThat(line.getColor()).isEqualTo(LINE_COLOR);
            softly.assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간.getDistance());
            softly.assertThat(line.getStations()).containsExactly(강남역, 선릉역);
        });
    }

    @Test
    @DisplayName("line 에 section 을 추가할 수 있다.")
    void addSectionTest() {
        line.addSection(선릉역_역삼역_구간);

        assertSoftly(softly -> {
            softly.assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간.getDistance() + 선릉역_역삼역_구간.getDistance());
            softly.assertThat(line.getStations()).containsExactly(강남역, 선릉역, 역삼역);
        });
    }

    @Test
    @DisplayName("line 에 section 을 제거할 수 있다.")
    void removeSectionByStationTest() {
        line.addSection(선릉역_역삼역_구간);

        line.removeSectionByStation(역삼역);

        assertSoftly(softly -> {
            softly.assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간.getDistance());
            softly.assertThat(line.getStations()).containsExactly(강남역, 선릉역);
        });
    }
}
