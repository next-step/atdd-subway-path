package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private Sections sections;
    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("신논현역");
        downStation = new Station("언주역");
        line = new Line("신분당선", "bg-red-600");

        sections = new Sections();
        sections.addSection(new Section(line, upStation, downStation, 10));
    }

    @DisplayName("노선에 구간이 하행 종점역에 정상적으로 등록되었습니다.")
    @Test
    void addSections() {
        // given
        Station newStation = new Station("신규역");

        // when
        sections.addSection(new Section(line, downStation, newStation, 3));

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("노선에 등록할 구간이 이미 존재하는 경우 예외처리를 한다.")
    @Test
    void checkDuplicationExistingSection() {
        // given
        Station duplicateUpStation = new Station("신논현역");
        Station duplicateDownStation = new Station("언주역");

        // when & then
        assertThatThrownBy(() -> sections.addSection(new Section(line, duplicateUpStation, duplicateDownStation, 3)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}