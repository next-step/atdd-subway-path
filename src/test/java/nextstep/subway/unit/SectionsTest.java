package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Line _5호선;
    private Station 군자역;
    private Station 아차산역;
    private Station 광나루역;
    private Station 천호역;
    private int distance;
    private Section section;
    private Sections sections;

    @BeforeEach
    void setup() {
        // given
        sections = new Sections();
        _5호선 = new Line("5호선", "파란색");
        군자역 = new Station("군자역");
        아차산역 = new Station("아차산역");
        광나루역 = new Station("광나루역");
        천호역 = new Station("천호역");
        distance = 10;
        section = Section.of(_5호선, 군자역, 아차산역, distance);
        sections.addSection(section);
    }

    @DisplayName("새로운 역을 구간의 중간에 추가")
    @Test
    void addSection() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 군자역, 광나루역, newSectionDistance);
        sections.addSection(newSection);

        assertThat(sections.getSections()).containsExactly(newSection, section);
        assertThat(sections.getSections().get(0).getDistance()).isEqualTo(newSectionDistance);
        assertThat(sections.getSections().get(1).getDistance()).isEqualTo(distance - newSectionDistance);
    }

    @DisplayName("새로운 역을 상행 종점으로 추가")
    @Test
    void addSection2() {
        Section newSection = Section.of(_5호선, 광나루역, 군자역, distance);
        sections.addSection(newSection);

        assertThat(sections.getSections()).containsExactly(newSection, section);
    }

    @DisplayName("새로운 역을 하행 종점으로 추가")
    @Test
    void addSection3() {
        Section newSection = Section.of(_5호선, 아차산역, 광나루역, distance);
        sections.addSection(newSection);

        assertThat(sections.getSections()).containsExactly(section, newSection);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다")
    @Test
    void addSectionFail() {
        int newSectionDistance = 11;
        Section newSection = Section.of(_5호선, 군자역, 광나루역, newSectionDistance);

        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void addSectionFail2() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 군자역, 아차산역, newSectionDistance);

        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행역과 하행역이 노선에 모두 등록되어 있지 않다면 추가할 수 없다")
    @Test
    void addSectionFail3() {
        int newSectionDistance = 3;
        Section newSection = Section.of(_5호선, 광나루역, 천호역, newSectionDistance);

        assertThatThrownBy(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        assertThat(sections.getStations()).hasSize(2);
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations2() {
        Section newSection = Section.of(_5호선, 아차산역, 광나루역, distance);
        sections.addSection(newSection);

        assertThat(sections.getStations()).hasSize(3);
    }
}
