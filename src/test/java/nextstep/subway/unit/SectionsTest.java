package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.sections.SectionsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionsTest {

    Line line;
    Station upStation;
    Station downStation;
    Section section;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        upStation = new Station(1L, "강남역");
        downStation = new Station(2L, "건대입구역");
        section = new Section(line, upStation, downStation, 10);
    }

    @DisplayName("구간을 성공적으로 추가한다")
    @Test
    public void add_section_test() {
        // given
        Sections sections = new Sections();

        // when
        sections.add(section);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("노선의 상행역과 신규 추가할 구간의 하행역이 동일할 경우 노선의 앞부분에 신규 구간을 추가할 수 있다.")
    @Test
    public void add_section_front_at_line() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(line, newStation, upStation, 5);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(newStation, upStation, downStation);
    }

    @DisplayName("노선의 하행역과 신규 추가할 구간의 상행역이 동일할 경우 노선의 뒷부분에 신규 구간을 추가할 수 있다.")
    @Test
    public void add_section_back_at_line() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(line, downStation, newStation, 5);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("신규 구간의 상행역이 기존 구간의 상행역과 동일할 경우 중간에 구간을 추가할 수 있다")
    @Test
    public void add_section_at_middle_of_line_when_same_up_station() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(line, upStation, newStation, 3);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, newStation, downStation);
    }

    @DisplayName("신규 구간의 상행역이 기존 구간의 상행역과 동일할 경우 중간에 구간을 추가할 수 있다")
    @Test
    public void add_section_at_middle_of_line_when_same_down_station() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(line, newStation, downStation, 3);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, newStation, downStation);
    }

    @DisplayName("중간에 구간을 추가할 때, 신규로 추가되는 구간의 길이가 기존 구간보다 길다면 예외를 던진다")
    @ValueSource(ints = {10, 11})
    @ParameterizedTest
    public void add_section_middle_at_line_fail(int overDistance) {
        // given
        Sections sections = new Sections();
        sections.add(section);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(line, upStation, newStation, overDistance);
        SectionsException exception = assertThrows(SectionsException.class, () -> sections.add(newSection));

        // then
        assertThat(exception).isInstanceOf(SectionsException.class);
    }

    @DisplayName("역 사이에 새로운 역 추가할때, 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    public void add_section_fail_by_already_register_up_down_stations() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        // when
        Section newSection = new Section(line, upStation, downStation, 3);
        SectionsException exception = assertThrows(SectionsException.class, () -> sections.add(newSection));

        // then
        assertThat(exception).isInstanceOf(SectionsException.class);
    }

    @DisplayName("역 사이에 새로운 역 추가할때, 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    public void add_section_fail_by_not_exist_up_down_stations() {
        // given
        Sections sections = new Sections();
        sections.add(section);
        Station 없는역1 = new Station(3L, "없는역1");
        Station 없는역2 = new Station(4L, "없는역2");

        // when
        Section newSection = new Section(line, 없는역1, 없는역2, 3);
        SectionsException exception = assertThrows(SectionsException.class, () -> sections.add(newSection));

        // then
        assertThat(exception).isInstanceOf(SectionsException.class);
    }

    @DisplayName("Sections에 등록된 모든 역을 조회한다")
    @Test
    public void get_all_station_in_section() {
        // given
        Sections sections = new Sections();
        sections.add(section);

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
        sections.add(section);

        // when
        sections.deleteLastSection(downStation);

        // then
        assertThat(sections.isEmptySections()).isTrue();
    }

    @DisplayName("하행종점역이 일치하지 않을때 Sections의 마지막 section을 삭제하려 하면 예외가 발생한다")
    @Test
    public void delete_last_section_when_not_same_down_station() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        // when
        Exception exception = assertThrows(SectionsException.class, () -> sections.deleteLastSection(upStation));

        // then
        assertThat(exception).isInstanceOf(SectionsException.class);
    }
}
