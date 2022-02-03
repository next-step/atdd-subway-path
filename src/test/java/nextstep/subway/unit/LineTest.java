package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        var line = new Line("신분당선", "bg-red-600");
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");

        var section1 = new Section(station1, station2, 10);
        line.init(section1);

        // when
        var station = new Station("양재역");
        var section = new Section(station2, station, 11);
        line.addSection(section);

        // then
        assertThat(line.getSections().size()).isEqualTo(2);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");

        var line = new Line("신분당선", "bg-red-600");
        var section = new Section(station1, station2, 10);
        line.init(section);

        // when
        var stationList = line.getStations();

        // then
        assertThat(stationList).containsExactlyInAnyOrder(station1, station2);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("강남역");

        var line = new Line("신분당선", "bg-red-600");
        var section = new Section(station1, station2, 10);
        line.init(section);

        var station3 = new Station("양재역");
        line.addSection(new Section(station2, station3, 10));

        // when
        line.remove(station3);

        // then
        assertThat(line.getStations()).doesNotContain(station3);
    }

    @DisplayName("구간 목록 처음에 새로운 구간을 추가할 경우")
    @Test
    void addSectionToTop() {
        // given
        var station1 = new Station("강남역");
        var station2 = new Station("양재역");

        var line = new Line("신분당선", "bg-red-600");
        var section = new Section(station1, station2, 10);
        line.init(section);

        // when
        var station = new Station("신논현역");
        line.addSection(new Section(station, station1, 11));

        // then
        assertThat(line.getStations()).containsExactlyInAnyOrder(station1, station2, station);
    }

    @DisplayName("구간의 중간에 새로운 구간을 추가할 경우")
    @Test
    void addSectionToBetween() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("양재역");

        var line = new Line("신분당선", "bg-red-600");
        var section = new Section(station1, station2, 10);
        line.init(section);

        // when
        var station = new Station("강남역");
        line.addSection(new Section(station1, station, 6));

        // then
        assertThat(line.getStations()).containsExactlyInAnyOrder(station1, station2, station);
    }

    @DisplayName("기존 구간의 중간에 추가할 때 더 긴 구간을 추가하면 예외 발생")
    @Test
    void addSectionToBetweenLong() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("양재역");

        var line = new Line("신분당선", "bg-red-600");
        var section = new Section(station1, station2, 10);
        line.init(section);

        // when, then
        var station = new Station("강남역");
        assertThrows(RuntimeException.class, () -> line.addSection(new Section(station1, station, 11)));
    }

    @DisplayName("추가하는 구간의 상행,하행 역 모두 현재 노선에 존재하지 않으면 예외 발생")
    @Test
    void addSectionWithoutStationsExist() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("양재역");

        var line = new Line("신분당선", "bg-red-600");
        var section = new Section(station1, station2, 10);
        line.init(section);

        // when, then
        var station3 = new Station("강남역");
        var station4 = new Station("신사역");
        assertThrows(RuntimeException.class, () -> line.addSection(new Section(station4, station3, 9)));
    }

    @DisplayName("추가하는 구간의 상행,하행 역 모두 현재 노선에 존재하면 예외 발생")
    @Test
    void addSectionWithStationsExist() {
        // given
        var station1 = new Station("신논현역");
        var station2 = new Station("양재역");

        var line = new Line("신분당선", "bg-red-600");
        var section = new Section(station1, station2, 10);
        line.init(section);

        // when, then
        assertThrows(RuntimeException.class, () -> line.addSection(new Section(station1, station2, 9)));
    }

}
