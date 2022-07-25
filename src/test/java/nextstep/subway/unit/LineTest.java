package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @DisplayName("노선에 구간 추가")
    @Test
    void addSection() {
        //given
        Line line = new Line("분당선", "bg-yellow-600");
        Section section = new Section(line, new Station("삼성역"), new Station("강남역"), 10);

        //when
        line.addSection(section);

        //then
        assertThat(line.getSections()).containsExactly(section);
    }

    @DisplayName("노선의 모든 지하철역 조회")
    @Test
    void getStations() {
        //given
        Line line = new Line("분당선", "bg-yellow-600");
        Station upStation = new Station("삼성역");
        Station downStation = new Station("강남역");
        line.addSection(new Section(line, upStation, downStation, 10));

        //when
        List<Station> stationList = line.getStations();

        //then
        assertThat(stationList).containsExactly(upStation, downStation);
    }

    @Test
    void removeSection() {
        //given
        Line line = new Line("분당선", "bg-yellow-600");
        Section section = new Section(line, new Station("삼성역"), new Station("강남역"), 10);
        line.addSection(section);

        //when
        line.removeSection(section);

        //then
        assertThat(line.getSections()).isEmpty();
    }
}
