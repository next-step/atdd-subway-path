package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SectionTest {

    Line line;
    Station upStation;
    Station downStation;
    Section section;

    @BeforeEach
    void setUp() {
        // given
        line = new Line("2호선", "green");
        upStation = new Station(1L, "강남역");
        downStation = new Station(2L, "건대입구역");
        section = new Section(line, upStation, downStation, 10);
    }

    @DisplayName("Section을 정상적으로 생성한다")
    @Test
    public void create_section_test() {
        // when
        Section section = new Section(line, upStation, downStation, 10);

        // then
        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(downStation)
        );
    }

    @DisplayName("두개의 구간을 합친다")
    @Test
    public void combine_two_station() {
        // given
        Station 신규역 = new Station(3L, "신규역");
        Section section2 = new Section(line, downStation, 신규역, 3);

        // when
        Section combineSection = Section.combineOf(section, section2);

        // then
        assertAll(
                () -> assertThat(combineSection.getDistance()).isEqualTo(13),
                () -> assertThat(combineSection.getUpStation()).isEqualTo(upStation),
                () -> assertThat(combineSection.getDownStation()).isEqualTo(신규역)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void create_section_with_invalid_distance(int distance) {
        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Section(line, upStation, downStation, distance));

        // then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간이 가지고 있는 하행종점역이 아닐경우 true를 반환한다")
    @Test
    public void dont_have_down_station() {
        // when
        boolean result = section.hasNotDownStation(upStation);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("상행구간이 동일한 신규 구간을 중간에 추가하면 신규-하행 구간을 반환한다")
    @Test
    public void divide_section_by_middle_station_at_front() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        Station 신규역 = new Station(3L, "신규역");
        Section newSection = new Section(line, upStation, 신규역, 3);

        // when
        Section halfBackSection = section.divideSectionByMiddle(newSection);

        // then
        assertAll(
                () -> assertThat(halfBackSection.getUpStation()).isEqualTo(신규역),
                () -> assertThat(halfBackSection.getDownStation()).isEqualTo(downStation),
                () -> assertThat(halfBackSection.getDistance()).isEqualTo(7)
        );
    }

    @DisplayName("하행구간이 동일한 신규 구간을 중간에 추가하면 상행-신규 구간을 반환한다")
    @Test
    public void divide_section_by_middle_station_at_back() {
        // given
        Sections sections = new Sections();
        sections.add(section);

        Station 신규역 = new Station(3L, "신규역");
        Section newSection = new Section(line, 신규역, downStation, 4);

        // when
        Section halfBackSection = section.divideSectionByMiddle(newSection);

        // then
        assertAll(
                () -> assertThat(halfBackSection.getUpStation()).isEqualTo(upStation),
                () -> assertThat(halfBackSection.getDownStation()).isEqualTo(신규역),
                () -> assertThat(halfBackSection.getDistance()).isEqualTo(6)
        );
    }
}
