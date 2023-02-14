package nextstep.subway.unit.domain.sections.strategy.remove;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.sections.Sections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoveTailSectionStrategyTest {

    private Line 이호선;
    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;
    private Station 논현역;
    private Station 건대입구역;

    private Sections sections;

    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "green");

        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");
        논현역 = new Station("논현역");
        건대입구역 = new Station("건대입구역");

        sections = new Sections();
        sections.add(new Section(이호선, 강남역, 삼성역, 10));

        Section 상행종착역_앞_section = new Section(이호선, 논현역, 강남역, 6);
        Section 상행종착역_뒤_section = new Section(이호선, 강남역, 잠실역, 6);
        Section 하행종착역_뒤_section = new Section(이호선, 삼성역, 건대입구역, 6);

        sections.add(상행종착역_앞_section);
        sections.add(상행종착역_뒤_section);
        sections.add(하행종착역_뒤_section);
    }

    @Test
    void remove() {
        // Given
        int beforeSize = sections.size();

        // When
        sections.delete(건대입구역);

        // Then
        assertThat(sections.size()).isEqualTo(beforeSize - 1);
        assertThat(sections.getStations()).containsExactly(논현역, 강남역, 잠실역, 삼성역);
    }
}