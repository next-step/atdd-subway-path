package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private final Line line = new Line("2호선", "green");
    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections();
    }

    @Test
    @DisplayName("구간 등록")
    void addSections() {
        //when
        구간_등록();

        //then
        assertThat(sections.sections()).hasSize(1);
    }

    private void 구간_등록() {
        sections.addSections(line, new Station("강남역"), new Station("역삼역"), 10);
    }

    @Test
    @DisplayName("구간이 있는경우 전 구간 조회")
    void getStationsNotEmpty() {
        구간_등록();

        assertThat(sections.getStations())
            .containsExactly(new Station("강남역"), new Station("역삼역"));
    }

    @Test
    @DisplayName("구간이 없는경우 빈 리스트 반환")
    void getStationsIsEmpty() {
        assertThat(sections.getStations()).isEmpty();
    }

    @Test
    @DisplayName("구간이 없는경우 삭제 시 예외")
    void removeStationsIsEmpty() {
        assertThatThrownBy(() -> sections.removeStations(new Station("강남역")))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("등록된 구간이 없습니다.");
    }

    @Test
    @DisplayName("구간 하행 종점역 정보가 맞지 않는 경우 예외")
    void removeStationsNotEqualDownStation() {
        구간_등록();
        assertThatThrownBy(() -> sections.removeStations(new Station("잠실역")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("하행 종점역 정보가 다릅니다.");
    }
}