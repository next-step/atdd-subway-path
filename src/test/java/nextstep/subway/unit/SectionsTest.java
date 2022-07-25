package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.sections.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    Line line;
    Station upStation;
    Station downStation;
    Section originSection;
    Sections sections;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        upStation = new Station(1L, "강남역");
        downStation = new Station(2L, "건대입구역");
        originSection = new Section(1L, line, upStation, downStation, 10);
        sections = new Sections();
    }

    @DisplayName("구간을 성공적으로 추가한다")
    @Test
    public void add_section_test() {
        // when
        sections.add(originSection);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("노선의 상행역과 신규 추가할 구간의 하행역이 동일할 경우 노선의 앞부분에 신규 구간을 추가할 수 있다.")
    @Test
    public void add_section_front_at_line() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(2L, line, newStation, upStation, 5);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(newStation, upStation, downStation);
    }

    @DisplayName("구간을 맨 앞에 추가한 후, 동일한 상행역을 통해 중간에 추가할 수 있다")
    @Test
    public void add_section_front_and_middle() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");

        Section newSection = new Section(2L, line, newStation, upStation, 5);
        sections.add(newSection);

        Station newStation2 = new Station(4L, "신규역2");
        Section newSection2 = new Section(3L, line, upStation, newStation2, 1);

        // when
        sections.add(newSection2);

        // then
        assertThat(sections.getStations()).containsExactly(newStation, upStation, newStation2, downStation);
    }

    @DisplayName("구간을 맨 앞에 추가한 후, 동일한 하행역을 통해 중간에 추가할 수 있다")
    @Test
    public void add_section_front_and_middle_2() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, newStation, upStation, 5);
        sections.add(newSection);

        Station newStation2 = new Station(4L, "신규역2");
        Section newSection2 = new Section(3L, line, newStation2, downStation, 1);

        // when
        sections.add(newSection2);

        // then
        assertThat(sections.getStations()).containsExactly(newStation, upStation, newStation2, downStation);
    }

    @DisplayName("상행역 앞에 하나, 상행역 바로 뒤에 두번의 역을 추가한다")
    @Test
    public void add_section_middle_3() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, newStation, upStation, 5);
        sections.add(newSection);

        Station newStation2 = new Station(4L, "신규역2");
        Section newSection2 = new Section(3L, line, upStation, newStation2, 6);
        sections.add(newSection2);

        Station newStation3 = new Station(5L, "신규역3");
        Section newSection3 = new Section(4L, line, upStation, newStation3, 2);

        // when
        sections.add(newSection3);

        // then
        assertThat(sections.getStations()).containsExactly(newStation, upStation, newStation3, newStation2, downStation);
    }

    @DisplayName("상행역 앞에 하나, 상행역 바로 뒤에 한번, 하행역 바로 앞에 한번 추가한다")
    @Test
    public void add_section_middle_4() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, newStation, upStation, 5);
        sections.add(newSection);

        Station newStation2 = new Station(4L, "신규역2");
        Section newSection2 = new Section(3L, line, upStation, newStation2, 6);
        sections.add(newSection2);

        Station newStation3 = new Station(5L, "신규역3");
        Section newSection3 = new Section(4L, line, newStation3, downStation, 2);

        // when
        sections.add(newSection3);

        // then
        assertThat(sections.getStations()).containsExactly(newStation, upStation, newStation2, newStation3, downStation);
    }

    @DisplayName("신규역을 상행 구간 앞으로 3번 추가할 수 있다")
    @Test
    public void add_section_front_3_times() {
        // given
        sections.add(originSection);
        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(2L, line, newStation, upStation, 8);
        sections.add(newSection);

        Station newStation2 = new Station(4L, "신규역2");
        Section newSection2 = new Section(3L, line, newStation, newStation2, 4);
        sections.add(newSection2);

        Station newStation3 = new Station(5L, "신규역3");
        Section newSection3 = new Section(4L, line, newStation2, newStation3, 2);
        sections.add(newSection3);

        //then
        assertThat(sections.getStations()).containsExactly(newStation, newStation2, newStation3, upStation, downStation);
    }

    @DisplayName("노선의 하행역과 신규 추가할 구간의 상행역이 동일할 경우 노선의 뒷부분에 신규 구간을 추가할 수 있다.")
    @Test
    public void add_section_back_at_line() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(2L, line, downStation, newStation, 5);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("신규 구간의 상행역이 기존 구간의 상행역과 동일할 경우 중간에 구간을 추가할 수 있다")
    @Test
    public void add_section_at_middle_of_line_when_same_up_station() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(2L, line, upStation, newStation, 3);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, newStation, downStation);
    }

    @DisplayName("신규 구간의 상행역이 기존 구간의 상행역과 동일할 경우 중간에 구간을 추가할 수 있다")
    @Test
    public void add_section_at_middle_of_line_when_same_down_station() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(2L, line, newStation, downStation, 3);
        sections.add(newSection);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, newStation, downStation);
    }

    @DisplayName("중간에 구간을 추가할 때, 신규로 추가되는 구간의 길이가 기존 구간보다 길다면 예외를 던진다")
    @ValueSource(ints = {10, 11})
    @ParameterizedTest
    public void add_section_middle_at_line_fail(int overDistance) {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");

        // when
        Section newSection = new Section(2L, line, upStation, newStation, overDistance);
        ThrowableAssert.ThrowingCallable actual = () -> sections.add(newSection);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(SectionDistanceException.class)
                .hasMessage("기존의 구간 길이보다 긴 신규구간을 중간에 추가할 수 없습니다");
    }

    @DisplayName("구간을 중간에 추가할때 기존의 거리보다 더 큰 구간이 추가되면 예외가 발생한다")
    @Test
    public void add_section_fail_by_distance() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, newStation, upStation, 5);
        sections.add(newSection);

        Station newStation2 = new Station(4L, "신규역2");
        Section newSection2 = new Section(3L, line, upStation, newStation2, 9);
        sections.add(newSection2);

        Station newStation3 = new Station(5L, "신규역3");
        Section newSection3 = new Section(4L, line, newStation2, newStation3, 3);

        //when
        ThrowableAssert.ThrowingCallable actual = () -> sections.add(newSection3);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(SectionDistanceException.class)
                .hasMessage("기존의 구간 길이보다 긴 신규구간을 중간에 추가할 수 없습니다");
    }

    @DisplayName("역 사이에 새로운 역 추가할때, 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    public void add_section_fail_by_already_register_up_down_stations() {
        // given
        sections.add(originSection);

        // when
        Section newSection = new Section(2L, line, upStation, downStation, 3);
        ThrowableAssert.ThrowingCallable actual = () -> sections.add(newSection);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(AlreadyBothStationRegisterException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다");
    }

    @DisplayName("역 사이에 새로운 역 추가할때, 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    public void add_section_fail_by_not_exist_up_down_stations() {
        // given
        sections.add(originSection);
        Station 없는역1 = new Station(3L, "없는역1");
        Station 없는역2 = new Station(4L, "없는역2");

        // when
        Section newSection = new Section(2L, line, 없는역1, 없는역2, 3);
        ThrowableAssert.ThrowingCallable actual = () -> sections.add(newSection);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundBothStationException.class)
                .hasMessage("상행역과 하행역 모두 찾을 수 없습니다");
    }

    @DisplayName("Sections에 등록된 모든 역을 조회한다")
    @Test
    public void get_all_station_in_section() {
        // given
        sections.add(originSection);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @DisplayName("Sections의 첫 section을 삭제할 수 있다")
    @Test
    public void delete_first_section() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, downStation, newStation, 5);
        sections.add(newSection);

        // when
        sections.deleteSection(upStation);

        // then
        assertThat(sections.getStations()).containsExactly(downStation, newStation);
    }

    @DisplayName("Sections의 마지막 section을 삭제할 수 있다")
    @Test
    public void delete_last_section() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, downStation, newStation, 5);
        sections.add(newSection);

        // when
        sections.deleteSection(newStation);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("연속된 두 구간의 중간역을 삭제할 수 있다")
    @Test
    public void delete_middle_station() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, downStation, newStation, 5);
        sections.add(newSection);

        // when
        sections.deleteSection(downStation);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, newStation);
    }

    @DisplayName("연속된 3 구간의 중간역을 삭제할 수 있다")
    @Test
    public void delete_middle_station_2() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, downStation, newStation, 5);
        sections.add(newSection);

        Station newStation2 = new Station(4L, "신규역2");
        Section newSection2 = new Section(3L, line, newStation, newStation2, 9);
        sections.add(newSection2);

        // when
        sections.deleteSection(newStation);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation, newStation2);
    }

    @DisplayName("구간이 하나 남았을때 삭제할 경우 예외가 발생한다.")
    @Test
    public void cant_delete_section_when_only_one_section() {
        // given
        sections.add(originSection);

        //when
        ThrowableAssert.ThrowingCallable actual = () -> sections.deleteSection(upStation);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(CantDeleteLastSectionException.class)
                .hasMessage("노선의 마지막 하나 남은 구간은 삭제할 수 없습니다");
    }

    @DisplayName("노선에 등록되지 않은 역을 삭제할 경우 예외가 발생한다.")
    @Test
    public void cant_delete_not_exists_section() {
        // given
        sections.add(originSection);

        Station newStation = new Station(3L, "신규역");
        Section newSection = new Section(2L, line, downStation, newStation, 5);
        sections.add(newSection);

        Station 노선에없는역 = new Station(4L, "노선에없는역");

        //when
        ThrowableAssert.ThrowingCallable actual = () -> sections.deleteSection(노선에없는역);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(NotFoundStationException.class)
                .hasMessage("해당 역을 찾을 수 없습니다");
    }
}
