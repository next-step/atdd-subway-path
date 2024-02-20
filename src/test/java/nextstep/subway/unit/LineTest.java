package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    @Test
    void addSection() {
        //given
        Line line = new Line("2호선", "green");

        Station upStation = new Station("강남역");
        Station downStation = new Station("선릉역");
        Station newStation = new Station("삼성역");

        Section initSection = new Section(upStation, downStation, 7);
        line.addSection(initSection);

        //when
        Section newSection = new Section(downStation, newStation, 3);
        line.addSection(newSection);

        //then
        assertThat(line.getSectionList()).hasSize(2);
    }

    @Test
    void getStations() {
        //given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("선릉역");
        Station newStation = new Station("삼성역");

        Section initSection = new Section(upStation, downStation, 7);
        line.addSection(initSection);

        Section newSection = new Section(downStation, newStation, 3);
        line.addSection(newSection);

        //when
        List<Station> stations = line.getStations();

        //then
        assertThat(stations).containsOnly(upStation, downStation, newStation);
    }

    @Test
    void removeSection() {
        //given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("선릉역");
        Station newStation = new Station("삼성역");

        Section initSection = new Section(upStation, downStation, 7);
        line.addSection(initSection);

        Section newSection = new Section(downStation, newStation, 3);
        line.addSection(newSection);

        //when
        line.deleteSection(newStation);

        //then
        assertThat(line.getSectionList()).hasSize(1);
    }

    @Test
    void addMiddleSection() {
        //given
        Line line = new Line("2호선", "green");

        Station upStation = new Station("강남역");
        Station downStation = new Station("선릉역");
        Station newStation = new Station("역삼역");

        Section initSection = new Section(upStation, downStation, 7);
        line.addSection(initSection);

        //when
        Section newSection = new Section(upStation, newStation, 3);
        line.addSection(newSection);

        //then
        assertAll(
                () -> assertThat(line.getSectionList()).hasSize(2),
                () -> assertThat(line.getSectionList()).containsOnly(new Section(upStation, newStation, 3, line), new Section(newStation, downStation, 4, line))
        );
    }

    @Test
    void deleteMiddleSection() {
        //given
        Line line = new Line("2호선", "green");

        Station 강남역 = new Station("강남역");
        Station 선릉역 = new Station("선릉역");
        Station 삼성역 = new Station("삼성역");

        Section 강남_선릉_구간 = new Section(강남역, 선릉역, 7);
        line.addSection(강남_선릉_구간);

        Section 선릉_삼성_구간 = new Section(선릉역, 삼성역, 3);
        line.addSection(선릉_삼성_구간);

        //when
        line.deleteSection(선릉역);

        //then
        assertAll(
                () -> assertThat(line.getSectionList()).hasSize(1),
                () -> assertThat(line.getSectionList()).containsOnly(new Section(강남역, 삼성역, 10, line))
        );
    }
}
