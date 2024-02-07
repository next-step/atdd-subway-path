package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 선릉역 = new Station(3L, "선릉역");

    private final Section 강남_역삼 = new Section(new Line(), 강남역, 역삼역, 10L);
    private final Section 역삼_선릉 = new Section(new Line(), 역삼역, 선릉역, 3L);
    private final Section 강남_선릉 = new Section(new Line(), 강남역, 선릉역, 20L);

    @DisplayName("구간 1개 추가")
    @Test
    void addSection() {
        Sections sections = new Sections();

        sections.addSection(강남_역삼);
        assertThat(sections.get()).hasSize(1);
    }

    @DisplayName("구간 1개_중간 역 추가")
    @Test
    void addSection_middle() {
        Sections sections = new Sections();
        sections.addSection(강남_선릉);
        sections.addSection(강남_역삼);

        assertThat(sections.allStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("구간 2개 추가_1개 삭제")
    @Test
    void deleteSection() {
        Sections sections = new Sections();
        sections.addSection(강남_역삼);
        sections.addSection(역삼_선릉);

        sections.deleteSection(3L);

        assertThat(sections.get()).hasSize(1);
    }

    @DisplayName("구간 2개 추가_3개 역 존재")
    @Test
    void getStations() {
        Sections sections = new Sections();
        sections.addSection(강남_역삼);
        sections.addSection(역삼_선릉);

        assertThat(sections.allStations()).containsExactly(강남역, 역삼역, 선릉역);
    }
}
