package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.AddSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.unit.fixture.StationFixture.*;
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
        assertThat(신분당선.getStations()).containsExactly(광교, 광교중앙);
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
        assertThat(신분당선.getStations()).containsExactly(광교, 광교중앙, 상현);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 : 하행 == 하행")
    void addSection_success2_1() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 상현, 5);

        // when
        신분당선.addSection(광교중앙, 상현, 3);

        // then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getStations()).containsExactly(광교, 광교중앙, 상현);
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 : 기존 구간 길이 <= 신규 구간 길이 : 예외 처리")
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
        assertThat(신분당선.getStations()).containsExactly(광교, 광교중앙, 상현);
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 : 상행 == 하행")
    void addSection_success4() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 광교중앙, 3);

        // when
        신분당선.addSection(광교중앙, 상현, 2);

        // then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getStations()).containsExactly(광교, 광교중앙, 상현);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음 : 예외 처리")
    void addSection_fail3() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 광교중앙, 3);

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(상현, 성복, 3))
                .isInstanceOf(RuntimeException.class);
    }

    /**
     * 상행 종점이 상행역인 구간을 먼저 찾는다.
     * 그 다음, 해당 구간의 하행역이 상행역인 다른 구간을 찾는다.
     * 2번을 반복하다가 하행 종점역을 찾으면 조회를 멈춘다.
     */
    @Test
    @DisplayName("노선 조회시 응답되는 역 목록")
    void getStations() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 광교중앙, 2);
        신분당선.addSection(광교중앙, 상현, 2);

        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations).containsExactly(광교, 광교중앙, 상현);
    }

    @Test
    @DisplayName("중간역이 제거될 경우")
    void delete_success() {
        // given
        Line 신분당선 = new Line(1L, "신분당", "RED");
        신분당선.addSection(광교, 광교중앙, 2);
        신분당선.addSection(광교중앙, 상현, 2);

        // when
        신분당선.delete(광교중앙);
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations).containsExactly(광교, 상현);
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(4);
    }

}
