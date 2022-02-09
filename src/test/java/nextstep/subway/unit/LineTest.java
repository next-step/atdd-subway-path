package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void deleteSection() {

        final Line line = new Line("a", "a");
        final Station aStation = station(1, "a"); // id 1
        final Station bStation = station(2, "b"); // id 2
        final Station cStation = station(3, "c"); // id 3
        final Station dStation = station(4, "d"); // id 4

        line.initSection(new Section(line, aStation, bStation, 1));
        line.addSection(new Section(line, bStation, cStation, 1));
        line.addSection(new Section(line, cStation, dStation, 1));
        line.deleteSection(2L); // a-b-c-d => a-c-d

        line.getSections(); // exception
    }

    private Station station(long id, String name) {
        final Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
