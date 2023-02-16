package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line 서울2호선 = new Line("2호선", "green");
    private Station 신촌역 = new Station("신촌역");
    private Station 당산역 = new Station("당산역");
    private Station 신도림역 = new Station("신도림역");
    private Station 신림역 = new Station("신림역");
    private Section section;

    // given
    @BeforeEach
    void setUp() {
        section = new Section(서울2호선, 당산역, 신도림역, 20);
        서울2호선.addSection(section);
    }

    @Test
    void addSection() {
        // then
        assertThat(서울2호선.getSectionsCount()).isEqualTo(1);

        Section firstSection = 서울2호선.getFirstSection();
        assertThat(firstSection.getUpStation()).isEqualTo(당산역);
        assertThat(firstSection.getDownStation()).isEqualTo(신도림역);
    }

    @Test
    void getStations() {
        // when
        서울2호선.addSection(new Section(서울2호선, 신도림역, 신림역, 10));
        서울2호선.addSection(new Section(서울2호선, 신촌역, 당산역, 5));

        // then
        assertThat(서울2호선.getStations()).containsExactly(신촌역, 당산역, 신도림역, 신림역);
    }

    @Test
    void removeSection() {
        // when
        서울2호선.removeSection(section.getDownStation());

        // then
        assertThat(서울2호선.getSectionsCount()).isEqualTo(0);
    }

}
