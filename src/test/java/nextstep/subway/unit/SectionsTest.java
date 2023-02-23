package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.exception.AddSectionException;
import nextstep.subway.exception.DeleteSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    @Test
    @DisplayName("지하철 구간 추가 : 성공")
    void addSection_success() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        Section 광교_상현 = new Section(신분당선, 광교, 상현, 10);
        Section 광교_광교중앙 = new Section(신분당선, 광교, 광교중앙, 5);
        Sections sections = new Sections(List.of(광교_상현));

        // when
        sections.addSection(광교_광교중앙);

        // then
        assertThat(sections.getStations()).containsExactly(광교, 광교중앙, 상현);
    }

    @Test
    @DisplayName("지하철 구간 추가 : 실패 - 구간 거리 초과")
    void addSection_fail() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        Section 광교_상현 = new Section(신분당선, 광교, 상현, 10);
        Section 광교_광교중앙 = new Section(신분당선, 광교, 광교중앙, 15);
        Sections sections = new Sections(List.of(광교_상현));

        // when & then
        assertThatThrownBy(() -> sections.addSection(광교_광교중앙)).isInstanceOf(AddSectionException.class);
    }

    @Test
    @DisplayName("지하철 목록 삭제 : 성공")
    void delete_success() {
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

    @Test
    @DisplayName("지하철 목록 삭제 : 실패 - 노선에 등록되지 않은 역")
    void delete_fail() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        Section 광교_광교중앙 = new Section(신분당선, 광교, 광교중앙, 10);
        Section 광교중앙_상현 = new Section(신분당선, 광교, 상현, 5);
        Sections sections = new Sections(List.of(광교_광교중앙, 광교중앙_상현));

        // when & then
        assertThatThrownBy(() -> sections.delete(성복)).isInstanceOf(DeleteSectionException.class);
    }
}
