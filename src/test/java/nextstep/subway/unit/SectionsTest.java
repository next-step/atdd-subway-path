package nextstep.subway.unit;

import nextstep.subway.Exception.LineException;
import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");
    private final Station 선릉역 = new Station(3L, "선릉역");

    private final Section 강남_역삼 = new Section(new Line(), 강남역, 역삼역, 10L);
    private final Section 역삼_선릉 = new Section(new Line(), 역삼역, 선릉역, 3L);
    private final Section 강남_선릉 = new Section(new Line(), 강남역, 선릉역, 20L);

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        Sections sections = new Sections();

        sections.addSection(강남_역삼);
        assertThat(sections.get()).hasSize(1);
    }

    @DisplayName("중간 구간 추가")
    @Test
    void addSection_middle() {
        Sections sections = new Sections();
        sections.addSection(강남_선릉);
        sections.addSection(강남_역삼);

        assertThat(sections.get().get(0).getDistance()).isEqualTo(10L);
        assertThat(sections.allStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("에러_중간 구간 추가_이미 존재하는 역")
    @Test
    void addSection_middle_error() {
        Sections sections = new Sections();
        sections.addSection(강남_선릉);
        sections.addSection(강남_역삼);

        assertThatThrownBy(() -> sections.addSection(new Section(new Line(), 역삼역, 강남역, 5L)))
                .isInstanceOf(LineException.class)
                .hasMessage("추가할 역이 이미 존재합니다.");
    }

    @DisplayName("처음 구간 추가")
    @Test
    void addSection_first() {
        Sections sections = new Sections();
        sections.addSection(역삼_선릉);
        sections.addSection(강남_역삼);
        assertThat(sections.allStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("에러_중복된 구간 추가")
    @Test
    void addSection_error_duplicatedSection() {
        Sections sections = new Sections();
        sections.addSection(강남_선릉);

        assertThatThrownBy(() -> sections.addSection(강남_선릉))
                .isInstanceOf(LineException.class)
                .hasMessage("이미 등록된 구간입니다.");
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection() {
        Sections sections = new Sections();
        sections.addSection(강남_역삼);
        sections.addSection(역삼_선릉);

        sections.deleteSection(3L);

        assertThat(sections.get()).hasSize(1);
    }

    @DisplayName("3개 역 존재")
    @Test
    void getStations() {
        Sections sections = new Sections();
        sections.addSection(강남_역삼);
        sections.addSection(역삼_선릉);

        assertThat(sections.allStations()).containsExactly(강남역, 역삼역, 선릉역);
    }
}
