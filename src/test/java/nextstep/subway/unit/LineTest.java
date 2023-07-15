package nextstep.subway.unit;

import static nextstep.subway.utils.LineFixture.*;
import static nextstep.subway.utils.StationFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Line line = new Line(1L, 신분당선_이름, 신분당선_이름);
        Station station = new Station(1L, 신사역);
        Station station2 = new Station(2L, 논현역);
        Section section = new Section(1L, line, station, station2, 10);

        // when
        line.addSection(section);

        // then
        assertThat(line.getStations()).usingRecursiveComparison()
            .isEqualTo(List.of(station, station2));
    }

    @DisplayName("지하철 노선에 등록된 지하철역들을 반환한다.")
    @Test
    void getStations() {
        // given
        Line line = new Line(1L, 신분당선_이름, 신분당선_이름);
        Station station = new Station(1L, 신사역);
        Station station2 = new Station(1L, 논현역);
        line.addSection(new Section(1L, line, station, station2, 10));

        // when
        List<Station> actual = line.getStations();

        // then
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(List.of(station, station2));
    }

    @DisplayName("지하철 노선에 해당 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        Line line = new Line(1L, 신분당선_이름, 신분당선_이름);
        line.addSection(new Section(1L, line, new Station(1L, 신사역), new Station(2L, 논현역), 15));
        line.addSection(new Section(2L, line, new Station(2L, 논현역), new Station(3L, 신논현역), 10));

        // when
        line.removeSection(3L);

        // then
        assertThat(line.getSectionSize()).isEqualTo(1);
    }
}
