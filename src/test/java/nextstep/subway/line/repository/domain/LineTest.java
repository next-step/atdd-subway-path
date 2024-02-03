package nextstep.subway.line.repository.domain;

import nextstep.subway.common.fixture.LineFactory;
import nextstep.subway.common.fixture.SectionFactory;
import nextstep.subway.common.fixture.StationFactory;
import nextstep.subway.station.repository.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class LineTest {
    private static final long 첫번째구간_ID = 1L;
    private static final long 두번째구간_ID = 2L;
    private static final long 강남역_ID = 1L;
    private static final long 선릉역_ID = 2L;
    private static final long 역삼역_ID = 3L;
    private static final int 첫번째구간_길이 = 10;
    private static final int 두번째구간_길이 = 20;
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
        강남역 = StationFactory.createStation(강남역_ID, "강남역");
        선릉역 = StationFactory.createStation(선릉역_ID, "선릉역");
        역삼역 = StationFactory.createStation(역삼역_ID, "선릉역");
        강남역_선릉역_구간 = SectionFactory.createSection(첫번째구간_ID, 강남역, 선릉역, 첫번째구간_길이);
        선릉역_역삼역_구간 = SectionFactory.createSection(두번째구간_ID, 선릉역, 역삼역, 두번째구간_길이);
        line = LineFactory.createLine(LINE_ID, LINE_NAME, LINE_COLOR, 강남역_선릉역_구간);
    }

    @Test
    @DisplayName("line 을 생성할 수 있다.")
    void lineCreateTest() {
        assertSoftly(softly -> {
            softly.assertThat(line.getId()).isEqualTo(LINE_ID);
            softly.assertThat(line.getName()).isEqualTo(LINE_NAME);
            softly.assertThat(line.getColor()).isEqualTo(LINE_COLOR);
            softly.assertThat(line.getDistance()).isEqualTo(첫번째구간_길이);
            softly.assertThat(line.getStations()).containsExactly(강남역, 선릉역);
        });
    }

    @Test
    @DisplayName("line 에 section 을 추가할 수 있다.")
    void addSectionTest() {
        line.addSection(선릉역_역삼역_구간);

        assertThat(line.getStations()).containsExactly(강남역, 선릉역, 역삼역);
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
