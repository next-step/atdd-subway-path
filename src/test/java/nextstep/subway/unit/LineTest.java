package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    @DisplayName("공통사항 : 기존 구간이 없을 경우 구간 등록 성공")
    void addSection_success1() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");

        // when
        신분당선.addSection(광교, 광교중앙, 1);

        // then
        assertThat(신분당선.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("공통사항 : 상행 == 상행 && 하행 == 하행 : 예외 처리")
    void addSection_fail1() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 광교중앙, 1);

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(광교, 광교중앙, 1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 : 상행 == 상행")
    void addSection_success2() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 상현, 5);

        // when
        신분당선.addSection(광교, 광교중앙, 3);

        // then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getSections().get(0).getUpStation().getId()).isEqualTo(광교.getId());
        assertThat(신분당선.getSections().get(0).getDownStation().getId()).isEqualTo(광교중앙.getId());
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(3);
        assertThat(신분당선.getSections().get(1).getUpStation().getId()).isEqualTo(광교중앙.getId());
        assertThat(신분당선.getSections().get(1).getDownStation().getId()).isEqualTo(상현.getId());
        assertThat(신분당선.getSections().get(1).getDistance()).isEqualTo(2);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 : 기존 구간 <= 신규 구간 : 예외 처리")
    void addSection_fail2() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 상현, 3);

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(광교, 광교중앙, 3))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 : 하행 == 상행")
    void addSection_success3() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교중앙, 상현, 2);

        // when
        신분당선.addSection(광교, 광교중앙, 3);

        // then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getSections().get(0).getUpStation().getId()).isEqualTo(광교.getId());
        assertThat(신분당선.getSections().get(0).getDownStation().getId()).isEqualTo(광교중앙.getId());
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(3);
        assertThat(신분당선.getSections().get(1).getUpStation().getId()).isEqualTo(광교중앙.getId());
        assertThat(신분당선.getSections().get(1).getDownStation().getId()).isEqualTo(상현.getId());
        assertThat(신분당선.getSections().get(1).getDistance()).isEqualTo(2);
    }

}
