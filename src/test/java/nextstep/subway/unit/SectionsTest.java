package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.acceptance.LineFixture.*;
import static nextstep.subway.acceptance.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("구간들 단위 테스트(Sections)")
class SectionsTest {

    private Station upStation;
    private Station downStation;
    private Line line;
    private Sections sections;

    @BeforeEach
    void setUp() {
        upStation = new Station(강남역);
        downStation = new Station(판교역);
        line = new Line(신분당선, 빨강색);
        sections = new Sections();
        sections.addSection(new Section(line, upStation, downStation, 강남_판교_거리));
    }

    @DisplayName("구간 목록 맨뒤에 새로운 구간을 추가할 경우")
    @Test
    void addSectionExtensionDownTerminalStation() {
        // given
        final Station extraStation = new Station(정자역);
        final Section section = new Section(line, downStation, extraStation, 판교_정자_거리);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation, extraStation);
    }

    @DisplayName("구간 목록 맨앞에 새로운 구간을 추가할 경우")
    @Test
    void addSectionExtensionUpTerminalStation() {
        // given
        final Station extraStation = new Station(논현역);
        final Section section = new Section(line, extraStation, upStation, 논현_강남_거리);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getStations()).containsExactly(extraStation, upStation, downStation);
    }

    @DisplayName("구간 목록 하행을 기준으로 중앙 방향의 새로운 구간을 추가할 경우")
    @Test
    void addSectionDownStation() {
        // given
        final Station extraStation = new Station(양재역);
        final Section section = new Section(line, extraStation, downStation, 양재_판교_거리);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, extraStation, downStation);
    }

    @DisplayName("구간 목록 상행을 기준으로 중앙 방향의 새로운 구간을 추가할 경우")
    @Test
    void addSectionUpStation() {
        // given
        final Station extraStation = new Station(양재역);
        final Section section = new Section(line, upStation, extraStation, 강남_양재_거리);

        // when
        sections.addSection(section);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, extraStation, downStation);
    }

    @DisplayName("구간 목록에 하행종점 구간 삭제")
    @Test
    void removeSectionByDownTerminalSection() {
        // given
        final Station extraStation = new Station(정자역);
        sections.addSection(new Section(line, downStation, extraStation, 판교_정자_거리));

        // when
        sections.removeSection(extraStation);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("구간 목록에 상행종점 구간 삭제")
    @Test
    void removeSectionByUpTerminalSection() {
        // given
        final Station extraStation = new Station(논현역);
        sections.addSection(new Section(line, extraStation, upStation, 논현_강남_거리));

        // when
        sections.removeSection(extraStation);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("구간 목록에 없는 역을 기준으로 구간을 삭제할 경우")
    @Test
    void removeSectionExcludeStation() {
        // given
        final Station extraStation = new Station(정자역);
        sections.addSection(new Section(line, downStation, extraStation, 판교_정자_거리));

        // when and then
        assertThatThrownBy(() -> sections.removeSection(new Station(논현역)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("removed station is not include sections");
    }

    @DisplayName("구간 목록에 구간이 1개일 때의 기준으로 구간을 삭제할 경우")
    @Test
    void removeSectionWhenOnlyOne() {
        // when and then
        assertThatThrownBy(() -> sections.removeSection(new Station(강남역)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("sections is not removable state");
    }
}
