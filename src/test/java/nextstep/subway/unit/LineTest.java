package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.fixture.unit.entity.LineFixture;
import nextstep.subway.fixture.unit.entity.StationFixture;
import org.junit.jupiter.api.Test;

class LineTest {
    @Test
    void addSection() {

        // given
        Line line = LineFixture.of("1호선", "green");
        Station upStation = StationFixture.of(1);
        Station downStation = StationFixture.of(2);
        int distance = 10;

        // when
        line.addSection(upStation, downStation, distance);

        // then
        assertThat(line.getSectionList()).hasSize(1);
    }


    @Test
    void getStations() {

        // given
        Line line = LineFixture.of("1호선", "green");
        Station upStation = StationFixture.of(1);
        Station downStation = StationFixture.of(2);

        int distance = 10;

        // when
        line.addSection(upStation, downStation, distance);

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @Test
    void removeSection() {

        // given
        Line line = LineFixture.of("1호선", "green");
        Station upStation = StationFixture.of(1);
        Station downStation = StationFixture.of(2);
        Station addStation = StationFixture.of(3);
        int distance = 10;

        // when
        line.addSection(upStation, downStation, distance);
        line.addSection(downStation, addStation, distance);
        line.removeSection(Section.of(line, downStation, addStation, distance).getDownStationId());

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }
}
