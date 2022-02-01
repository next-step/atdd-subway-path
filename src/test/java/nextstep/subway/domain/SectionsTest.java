package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간들 단위 테스트(Sections)")
class SectionsTest {

    private Station upStation;
    private Station downStation;
    private Line line;
    private Sections sections;

    @BeforeEach
    void setUp() {
        upStation = new Station("upStation");
        downStation = new Station("downStation");
        line = new Line("color", "name");
        sections = new Sections();
        sections.addSection(new Section(line, upStation, downStation, 10));
    }

    @DisplayName("구간 목록 맨뒤에 새로운 구간을 추가할 경우")
    @Test
    void addSectionLast() {
        // given
        final Station extraStation = new Station("extraStation");
        final Section section = new Section(line, downStation, extraStation, 1);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation, extraStation);
    }

    @DisplayName("구간 목록 맨앞에 새로운 구간을 추가할 경우")
    @Test
    void addSectionFirst() {
        // given
        final Station extraStation = new Station("extraStation");
        final Section section = new Section(line, extraStation, upStation, 1);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getStations()).containsExactly(extraStation, upStation, downStation);
    }
}
