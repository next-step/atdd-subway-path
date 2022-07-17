package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionsTest {

    Line line;
    Station upStation;
    Station downStation;
    Section newSection;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        upStation = new Station(1L, "강남역");
        downStation = new Station(2L, "건대입구역");
        newSection = new Section(line, upStation, downStation, 10);
    }

    @DisplayName("구간을 성공적으로 추가한다")
    @Test
    public void add_section_test() {
        // given
        Sections sections = new Sections();

        // when
        sections.add(newSection);

        // then
        assertAll(() -> {
            assertThat(sections.getSections()).hasSize(1);
            assertThat(sections.getSections()).containsExactly(newSection);
        });
    }

    @DisplayName("Sections에 등록된 모든 역을 조회한다")
    @Test
    public void get_all_station_in_section() {
        // given
        Sections sections = new Sections();
        sections.add(newSection);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @DisplayName("Sections의 마지막 section을 삭제할 수 있다")
    @Test
    public void delete_last_section() {
        // given
        Sections sections = new Sections();
        sections.add(newSection);

        // when
        sections.deleteLastSection(downStation);

        // then
        assertThat(sections.getSections()).isEmpty();
    }

    @DisplayName("하행종점역이 일치하지 않을때 Sections의 마지막 section을 삭제하려 하면 예외가 발생한다")
    @Test
    public void delete_last_section_when_not_same_down_station() {
        // given
        Sections sections = new Sections();
        sections.add(newSection);

        // when
        Exception exception = assertThrows(IllegalStateException.class, () -> sections.deleteLastSection(upStation));

        // then
        assertThat(exception).isInstanceOf(IllegalStateException.class);
    }
}
