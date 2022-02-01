package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
