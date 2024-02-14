package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    /**
     *  Given 지하철역과 노선, 구간을 생성하고
     *  When 노선에 구간을 추가할때
     *  Then 노선의 구간 조회 시, 추가된 역들을 조회할 수 있다.
     */
    @Test
    void addSection() {
        // given
        Station firstStation = new Station("firstStation");
        Station secondStation = new Station("secondStation");

        Line firstLine = new Line("firstLine", "blue");

        Section firstSection = new Section(firstStation.getId(), secondStation.getId(), 10);

        // when
        firstLine.getSections().addSection(firstSection);

        // then
        assertThat(firstLine.getSections().getSections().size()).isEqualTo(1);
        assertThat(firstLine.getSections().getStations()).containsAnyOf(firstStation.getId(), secondStation.getId());
    }

    /**
     *  Given 노선에 구간을 2개 추가하고
     *  When 노선의 구간에 포함된 역을 조회할 때,
     *  Then 추가된 2개의 구간과 역들을 조회할 수 있다.
     */
    @Test
    void getStations() {
        // given
        Station firstStation = new Station("firstStation");
        Station secondStation = new Station("secondStation");
        Station thirdStation = new Station("thirdStation");

        Line firstLine = new Line("firstLine", "blue");

        Section firstSection = new Section(firstStation.getId(), secondStation.getId(), 10);
        Section secondSection = new Section(secondStation.getId(), thirdStation.getId(), 20);
        firstLine.getSections().addSection(firstSection);
        firstLine.getSections().addSection(secondSection);

        // when
        List<Long> response = firstLine.getSections().getStations();

        // then
        assertThat(response.size()).isEqualTo(3);
        assertThat(response).containsAnyOf(firstStation.getId(), secondStation.getId(), thirdStation.getId());
    }

    /**
     *  Given 노선에 구간을 2개 추가하고
     *  When 노선의 구간을 제거할 때,
     *  Then 남아있는 구간만 조회할 수 있다.
     */
    @Test
    void removeSection() {
        // given
        Station firstStation = new Station("firstStation");
        Station secondStation = new Station("secondStation");
        Station thirdStation = new Station("thirdStation");

        Line firstLine = new Line("firstLine", "blue");

        Section firstSection = new Section(firstStation.getId(), secondStation.getId(), 10);
        Section secondSection = new Section(secondStation.getId(), thirdStation.getId(), 20);
        firstLine.getSections().addSection(firstSection);
        firstLine.getSections().addSection(secondSection);

        // when
        firstLine.getSections().removeSection();

        // then
        assertThat(firstLine.getSections().getSections().size()).isEqualTo(1);
        assertThat(firstLine.getSections().getStations()).containsAnyOf(firstStation.getId(), secondStation.getId());
    }
}
