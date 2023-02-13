package nextstep.subway.unit.domain.sections;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsRemoveTest {

    private Line 이호선;
    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;

    private Sections sections;
    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "green");

        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");

        sections = new Sections();
        sections.add(new Section(이호선, 강남역, 삼성역, 10));

        잠실역 = new Station("잠실역");
    }

    @Test
    void remove_exception_한개의_구간만_존재시() {
        // When & Then
        assertThatThrownBy(() -> sections.remove(강남역))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void remove_exception_기준할_역이_없을때() {
        // Given
        Station 부산역 = new Station("부산역");

        // When & Then
        assertThatThrownBy(() -> sections.remove(부산역))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void removeTailSection() {
        // Given
        Section 상행종착역_뒤_section = new Section(이호선, 강남역, 잠실역, 6);
        sections.add(상행종착역_뒤_section);
        int beforeSize = sections.size();

        // When
        sections.remove(삼성역);

        // Then
        assertThat(sections.size()).isEqualTo(beforeSize - 1);
        assertThat(sections.findSectionOnUpStation(강남역).get().getDistance()).isEqualTo(6);
    }

    @Test
    void removeHeadSection() {
        // Given
        Section 상행종착역_뒤_section = new Section(이호선, 강남역, 잠실역, 6);
        sections.add(상행종착역_뒤_section);
        int beforeSize = sections.size();

        // When
        sections.remove(강남역);

        // Then
        assertThat(sections.size()).isEqualTo(beforeSize - 1);
        assertThat(sections.findSectionOnUpStation(잠실역).get().getDistance()).isEqualTo(4);
    }

    @Test
    void removeInternalSection() {
        // Given
        Section 상행종착역_뒤_section = new Section(이호선, 강남역, 잠실역, 6);
        sections.add(상행종착역_뒤_section);
        int beforeSize = sections.size();

        // When
        sections.remove(잠실역);
        List<Section> sections1 = sections.getSections();
        for (Section section : sections1) {
            System.out.println("section = " + section);
        }
        // Then
        assertThat(sections.size()).isEqualTo(beforeSize - 1);
        assertThat(sections.findSectionOnUpStation(강남역).get().getDistance()).isEqualTo(10);
    }
}
