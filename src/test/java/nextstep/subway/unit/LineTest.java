package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    Station 신림역;
    Station 봉천역;
    Station 서울대입구역;
    Line line;
    Section section;
    int distance = 10;

    @BeforeEach
    void setUp() {
        신림역 = new Station("신림역");
        봉천역 = new Station("봉천역");
        서울대입구역 = new Station("서울대입구역");
        line = new Line("2호선", "green");
        section = new Section(line, 신림역, 봉천역, distance);

        line.addSection(section);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void addFirstSection() {
        // given
        Section newSection = new Section(line, 서울대입구역, 신림역, 10);

        // when
        line.addSection(newSection);

        // then
        assertSection(line, 서울대입구역, 신림역);
        assertSection(line, 신림역, 봉천역);

    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addLastSection() {
        // given
        Section newSection = new Section(line, 봉천역, 서울대입구역, 10);

        // when
        line.addSection(newSection);

        // then
        assertSection(line, 신림역, 봉천역);
        assertSection(line, 봉천역, 서울대입구역);
    }

    @DisplayName("구간 목록 중간에 새로운 구간을 추가할 경우")
    @Test
    void addMiddleSection() {
        // given
        Section targetSection = new Section(line, 봉천역, 서울대입구역, distance);
        line.addSection(targetSection);
        int newDistance = 7;
        Station 낙성대역 = new Station("낙성대역");
        Section newSection = new Section(line, 봉천역, 낙성대역, newDistance);

        // when
        line.addSection(newSection);

        // then
        assertSection(line, 신림역, 봉천역);
        assertSection(line, 봉천역, 낙성대역);
        assertSection(line, 낙성대역, 서울대입구역);
        assertThat(targetSection.getDistance()).isEqualTo(distance - newDistance);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        line.addSection(new Section(line, 봉천역, 서울대입구역, 10));

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).extracting(Station::getName)
                .containsOnly(신림역.getName(), 봉천역.getName(), 서울대입구역.getName());
    }

    @DisplayName("구간의 목록에서 첫 번째역 삭제")
    @Test
    void removeSectionFirst() {
        // given
        line.addSection(new Section(line, 봉천역, 서울대입구역, 10));

        // when
        line.removeSection(신림역);

        // then
        assertSection(line, 봉천역, 서울대입구역);
    }

    @DisplayName("구간의 목록에서 마지막역 삭제")
    @Test
    void removeSectionLast() {
        // given
        line.addSection(new Section(line, 봉천역, 서울대입구역, 10));

        // when
        line.removeSection(서울대입구역);

        // then
        assertSection(line, 신림역, 봉천역);
    }

    @DisplayName("구간의 목록에서 중간역 삭제")
    @Test
    void removeSectionMiddle() {
        // given
        int newDistance = 10;
        Section newSection = new Section(line, 봉천역, 서울대입구역, newDistance);
        line.addSection(newSection);

        // when
        line.removeSection(봉천역);

        // then
        assertSection(line, 신림역, 서울대입구역);
        assertThat(newSection.getDistance()).isEqualTo(distance + newDistance);
    }

    private void assertSection(Line line, Station upStation, Station downStation) {
        assertThat(line.getSections())
                .filteredOn(s -> equalsSection(s, upStation, downStation))
                .hasSize(1);
    }

    private boolean equalsSection(Section section, Station upStation, Station downStation) {
        return section.getUpStation().getName().equals(upStation.getName()) &&
                section.getDownStation().getName().equals(downStation.getName());
    }
}
