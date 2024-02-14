package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;
import nextstep.subway.exception.IllegalSectionException;

class LineTest {
    @Test
    void addSection_노선에_구간을_추가할_수_있다() {
        //given
        Station upStation = new Station("역삼역");
        Station downStation = new Station("선릉역");
        Section section = new Section(upStation, downStation, 10);
        Line line = new Line("이호선", "초록색");
        //when
        line.addSection(section);

        //then
        Sections sections = line.getSections();
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(1),
            () -> assertThat(sections.getLastSection()).isEqualTo(section)
        );
    }

    @Test
    void addSection_기존_노선의_하행종점역과_추가_구간의_상행역이_다르면_예외를_반환한다() {
        //given
        Station upStation = new Station("역삼역");
        Station downStation = new Station("선릉역");
        Section existingSection = new Section(upStation, downStation, 10);
        Line line = new Line("이호선", "초록색");
        line.addSection(existingSection);

        //when
        Station newUpStation = new Station("삼성역");
        Station newDownStation = new Station("잠실역");
        Section newSection = new Section(newUpStation, newDownStation, 2);

        //then
        assertThatThrownBy(() -> line.addSection(newSection))
            .isInstanceOf(IllegalSectionException.class);
    }

    @Test
    void getStations() {
        //given
        Station upStation = new Station("역삼역");
        Station downStation = new Station("선릉역");
        Section existingSection = new Section(upStation, downStation, 10);
        Line line = new Line("이호선", "초록색");
        line.addSection(existingSection);

        //when
        Stations stations = line.getStations();

        assertAll(
            () -> assertThat(stations.getStations()).hasSize(2),
            () -> assertThat(stations.getStations()).contains(upStation, downStation)
        );

    }

    @Test
    void testDeleteLastSection() {
        Station upStation1 = new Station("역삼역");
        Station downStation1 = new Station("선릉역");
        Station downStation2 = new Station("잠실역");
        Section section1 = new Section(upStation1, downStation1, 10);
        Section section2 = new Section(downStation1, downStation2, 2);
        String lineName = "이호선";
        String lineColor = "초록색";
        Line line = new Line(lineName, lineColor);
        line.addSection(section1);
        line.addSection(section2);

        //when
        line.deleteLastSection(downStation2);

        //then
        Sections sections = line.getSections();
        assertAll(
            () -> assertThat(sections.getSections()).hasSize(1),
            () -> assertThat(sections.getSections().get(0)).isEqualTo(section1)
        );
    }
}
