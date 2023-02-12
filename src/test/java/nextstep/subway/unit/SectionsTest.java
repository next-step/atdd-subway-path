package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BothSectionStationsNotExistsInLineException;
import nextstep.subway.exception.SectionStationsAlreadyExistsInLineException;
import nextstep.subway.exception.SectionWithStationNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Line line;
    private Station station0;
    private Station station1;
    private Station station1_5;
    private Station station2;
    private Station station3;

    @BeforeEach
    void setUp() {
        line = new Line("line", "green");
        station0 = new Station("station0");
        station1 = new Station("station1");
        station1_5 = new Station("station1.5");
        station2 = new Station("station2");
        station3 = new Station("station3");
    }

    @DisplayName("등록된 지하철 구간이 없는지 확인한다.")
    @Test
    void isEmpty() {
        // given
        Sections sections = new Sections();

        // when & then
        assertThat(sections.isEmpty()).isTrue();
    }

    @DisplayName("지하철 구간이 하나라도 등록되어 있으면, 지하철 구간 목록은 비어있지 않다.")
    @Test
    void isNotEmpty() {
        // given
        Sections sections = new Sections();

        // when
        sections.add(createSection(station1, station2));

        // when & then
        assertThat(sections.isEmpty()).isFalse();
    }

    @DisplayName("새로운 지하철 구간을 등록한다.")
    @Test
    void add() {
        // given
        Sections sections = new Sections();

        // when
        sections.add(createSection(station1, station2));

        // then
        assertThat(sections.getSections()).hasSize(1);
    }

    /**
     * AS-IS    1 ------------------ 2
     *        + 1 ------ 1.5
     * TO-BE    1 ------ 1.5 ------- 2
     */
    @DisplayName("기존 구간의 상행역을 기준으로 새로운 구간을 추가한다.")
    @Test
    void addIntermediateSectionExistingUpStation() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2, 5));

        // when
        sections.add(createSection(station1_5, station2, 1));

        // then
        assertThat(sections.getStationsInOrder()).containsExactly(station1, station1_5, station2);
    }

    /**
     * AS-IS    1 ------------------ 2
     *        +          1.5 ------- 2
     * TO-BE    1 ------ 1.5 ------- 2
     */
    @DisplayName("기존 구간의 하행역을 기준으로 새로운 구간을 추가한다.")
    @Test
    void addIntermediateSectionExistingDownStation() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2, 5));

        // when
        sections.add(createSection(station1_5, station2, 1));

        // then
        assertThat(sections.getStationsInOrder()).containsExactly(station1, station1_5, station2);
    }

    /**
     * AS-IS              1 ------- 2
     *        + 0 ------- 1
     * TO-BE    0 ------- 1 ------- 2
     */
    @DisplayName("새로운 역을 상행 종점으로 갖는 구간을 등록한다.")
    @Test
    void addSectionWithFirstUpStation() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));

        // when
        sections.add(createSection(station0, station1));

        // then
        assertThat(sections.getStationsInOrder()).containsExactly(station0, station1, station2);
    }

    /**
     * AS-IS    1 ------- 2
     * +                  2 ------- 3
     * TO-BE    1 ------- 2 ------- 3
     */
    @DisplayName("새로운 역을 하행 종점으로 갖는 구간을 등록한다.")
    @Test
    void addSectionWithLastDownStation() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));

        // when
        sections.add(createSection(station2, station3));

        // then
        assertThat(sections.getStationsInOrder()).containsExactly(station1, station2, station3);
    }

    @DisplayName("새로운 구간 추가 시, 상행역과 하행역 모두 노선에 등록되어 있지 않아야 한다.")
    @Test
    void bothStationsExistInLine() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));

        Section newSection = createSection(station1, station2);

        // when & then
        assertThatThrownBy(() -> sections.add(newSection))
            .isInstanceOf(SectionStationsAlreadyExistsInLineException.class);
    }

    @DisplayName("새로운 구간 추가 시, 상행역과 하행역 둘 중 하나는 노선에 포함되어 있어야 한다.")
    @Test
    void bothStationsNotExistInLine() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));

        Section newSection = createSection(station0, station3);

        // when & then
        assertThatThrownBy(() -> sections.add(newSection))
            .isInstanceOf(BothSectionStationsNotExistsInLineException.class);
    }

    @DisplayName("상행 종점역을 포함하는 지하철 구간을 제거한다.")
    @Test
    void removeFirstLineSection() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));
        sections.add(createSection(station2, station3));

        // when
        sections.remove(station1);

        // then
        assertThat(sections.getStationsInOrder()).containsExactly(station2, station3);
    }

    @DisplayName("하행 종점역을 포함하는 지하철 구간을 제거한다.")
    @Test
    void removeLastLineSection() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));
        sections.add(createSection(station2, station3));

        // when
        sections.remove(station3);

        // then
        assertThat(sections.getStationsInOrder()).containsExactly(station1, station2);
    }

    @DisplayName("지하철 노선의 중간역을 제거한다.")
    @Test
    void removeIntermediateLineSection() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));
        sections.add(createSection(station2, station3));

        // when
        sections.remove(station2);

        // then
        assertThat(sections.getStationsInOrder()).containsExactly(station1, station3);
    }

    @DisplayName("현재 등록된 지하철 구간이 하나인 경우, 지하철 구간을 제거할 수 없다.")
    @Test
    void cannotRemoveWhenSingleSection() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));

        // when & then
        assertThatThrownBy(() -> sections.remove(station2))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선에 등록된 역이 아니라면, 지하철 구간을 제거할 수 없다.")
    @Test
    void cannotRemoveNotRegisteredStation() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station1, station2));
        sections.add(createSection(station2, station3));

        // when & then
        assertThatThrownBy(() -> sections.remove(station1_5))
            .isInstanceOf(SectionWithStationNotExistsException.class);
    }

    @DisplayName("등록된 모든 지하철 구간의 역 목록을 순서대로 조회한다.")
    @Test
    void getStationsInOrder() {
        // given
        Sections sections = new Sections();
        sections.add(createSection(station0, station1));
        sections.add(createSection(station1, station2));
        sections.add(createSection(station2, station3));

        // when & then
        assertThat(sections.getStationsInOrder()).containsExactly(station0, station1, station2, station3);
    }

    private Section createSection(Station upStation, Station downStation) {
        return createSection(upStation, downStation, 1);
    }

    private Section createSection(Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }
}
