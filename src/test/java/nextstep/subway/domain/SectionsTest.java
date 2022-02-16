package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    Line line1;
    Station 상행역;
    Station 중간역;
    Station 하행역;

    Section firstSection;
    Section lastSection;

    Sections sections;

    @BeforeEach
    public void setup() {
        line1 = new Line("1호선", "blue");
        상행역 = new Station("상행역");
        중간역 = new Station("중간역");
        하행역 = new Station("하행역");

        firstSection = new Section(line1, 상행역, 중간역, 3);
        lastSection = new Section(line1, 중간역, 하행역, 3);

        ArrayList<Section> sectionList = new ArrayList() {{
            add(firstSection);
            add(lastSection);
        }};
        sections = new Sections(sectionList);
    }

    @DisplayName("상행 종점역 구간 제거")
    @Test
    void removeIfFirstSection() {
        // when
        sections.deleteSection(상행역);

        // then
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("하행 종점역 구간 제거")
    @Test
    void removeIfLastSection() {
        // when
        // 상행역 - 중간역 - 하행역
        sections.deleteSection(하행역);

        // then
        // 상행역 - 중간역
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("중간 역 구간 제거")
    @Test
    void removeIfMiddleSection() {
        // when
        sections.deleteSection(중간역);

        // then
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("구간이 하나인 노선 삭제 실패")
    @Test
    void removeSectionFail() {
        final ArrayList<Section> sectionList = new ArrayList() {{
            add(firstSection);
        }};
        final Sections sections = new Sections(sectionList);

        assertThatThrownBy(() -> {
            sections.deleteSection(상행역);
        });
    }

    @DisplayName("노선에 등록되지 않은 역 삭제 실패")
    @Test
    void removeSectionFail2() {
        final Station 또다른역 = new Station("또다른역");

        assertThatThrownBy(() -> {
            sections.deleteSection(또다른역);
        });
    }

    @DisplayName("노선 삭제시 거리는 두 구간의 distance 합")
    @Test
    void distance() {
        // when
        // 상행역 -(3)- 중간역 -(3) 하행역
        sections.deleteSection(중간역);

        // then
        // 상행역 -(6)- 하행
        assertThat(sections.getSectionFromDownStation(하행역).get().getDistance()).isEqualTo(6);
    }
}