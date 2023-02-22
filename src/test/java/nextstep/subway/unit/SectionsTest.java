package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("지하철 구간 추가")
    void addSection() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        Section 광교_상현 = new Section(신분당선, 광교, 상현, 10);
        Section 광교_광교중앙 = new Section(신분당선, 광교, 광교중앙, 5);
        Sections sections = new Sections(List.of(광교_상현));

        // when
        System.out.println(sections.getSections().get(0).getUpStation().getName());
        sections.addSection(광교_광교중앙);

        // then
        assertThat(sections.getStations()).containsExactly(광교, 광교중앙, 상현);
    }

    @Test
    @DisplayName("지하철 목록 삭제")
    void delete() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        Section 광교_광교중앙 = new Section(신분당선, 광교, 광교중앙, 10);
        Section 광교중앙_상현 = new Section(신분당선, 광교, 상현, 5);
        Sections sections = new Sections(List.of(광교_광교중앙, 광교중앙_상현));

        // when
        sections.delete(광교중앙);

        // then
        assertThat(sections.getStations()).containsExactly(광교, 상현);
    }
}
