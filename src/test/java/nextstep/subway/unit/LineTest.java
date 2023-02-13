package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.StationFixture.광교;
import static nextstep.subway.unit.StationFixture.광교중앙;
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
        assertThat(신분당선.getSections().get(0).getUpStation().getId()).isEqualTo(광교.getId());
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
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
