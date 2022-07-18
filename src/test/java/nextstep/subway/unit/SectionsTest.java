package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {

    @Test
    void 구간을_추가한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);
        Sections sections = new Sections();

        // when
        sections.addSection(section);

        // then
        assertAll(() -> {
            assertThat(sections.getSections()).hasSize(1);
            assertThat(sections.getSections()).containsExactly(section);
        });
    }

    @Test
    void 여러_구간을_추가한다() {
        // given
        Line line = new Line("2호선", "green");
        Station station1 = new Station("암사역");
        Station station2 = new Station("모란역");
        Station station3 = new Station("숭실대역");
        Station station4 = new Station("송파역");
        Section section1 = new Section(line, station1, station2, 10);
        Section section2 = new Section(line, station2, station3, 4);
        Section section3 = new Section(line, station2, station4, 4);
        Sections sections = new Sections();

        // when
        sections.addSection(section1);
        sections.addSection(section2);
        sections.addSection(section3);

        // then
        assertAll(() -> {
            assertThat(sections.getSections()).hasSize(3);
            assertThat(sections.getSections()).containsExactly(section1, section3, section2);
        });
    }

    @Test
    void 하행역_기준으로_구간을_추가한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation1 = new Station("모란역");
        Station downStation2 = new Station("숭실대역");
        Section section1 = new Section(line, upStation, downStation1, 10);
        Section section2 = new Section(line, downStation1, downStation2, 4);
        Sections sections = new Sections();

        // when
        sections.addSection(section1);
        sections.addSection(section2);

        // then
        assertAll(() -> {
            assertThat(sections.getSections()).hasSize(2);
            assertThat(sections.getSections()).containsExactly(section1, section2);
        });
    }

    @Test
    void 상행역_기준으로_구간을_추가한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation1 = new Station("숭실대역");
        Station downStation1 = new Station("모란역");
        Station upStation2 = new Station("암사역");
        Section section1 = new Section(line, upStation2, upStation1, 10);
        Section section2 = new Section(line, upStation1, downStation1, 4);
        Sections sections = new Sections();

        // when
        sections.addSection(section1);
        sections.addSection(section2);

        // then
        assertAll(() -> {
            assertThat(sections.getSections()).hasSize(2);
            assertThat(sections.getSections()).containsExactly(section1, section2);
        });
    }

    @Test
    void 구간을_추가시_거리가_같으면_예외를_발생시킨다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation1 = new Station("모란역");
        Station downStation2 = new Station("숭실대역");
        Section section1 = new Section(line, upStation, downStation1, 10);
        Section section2 = new Section(line, upStation, downStation2, 10);
        Sections sections = new Sections();

        // when
        sections.addSection(section1);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                sections.addSection(section2)
        );
    }

    @Test
    void 구간을_추가시_같은_구간인_경우_예외를_발생시킨다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);
        Section section2 = new Section(line, upStation, downStation, 10);
        Sections sections = new Sections();

        // when
        sections.addSection(section);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                sections.addSection(section2)
        );
    }

    @Test
    void 구간을_추가시_상행역_하행역_모두_일치하지_않으면_예외를_발생시킨다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation1 = new Station("암사역");
        Station downStation1 = new Station("모란역");
        Section section = new Section(line, upStation1, downStation1, 10);

        Station upStation2 = new Station("숭실대역");
        Station downStation2 = new Station("송파역");
        Section section2 = new Section(line, upStation2, downStation2, 10);

        Sections sections = new Sections();

        // when
        sections.addSection(section);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                sections.addSection(section2)
        );
    }

    @Test
    void 구간에서_등록된_역을_조회한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation1 = new Station("모란역");
        Station downStation2 = new Station("숭실대역");
        Section section1 = new Section(line, upStation, downStation1, 10);
        Section section2 = new Section(line, downStation1, downStation2, 4);
        Sections sections = new Sections();

        // when
        sections.addSection(section1);
        sections.addSection(section2);

        // then
        assertThat(sections.getStations()).containsExactly(upStation, downStation1, downStation2);
    }

    @Test
    void 구간에_등록된_역을_조회한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);
        Sections sections = new Sections();
        sections.addSection(section);

        // when
        List<Station> stations = sections.getStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @Test
    void 구간을_삭제한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);
        Sections sections = new Sections();
        sections.addSection(section);

        // when
        sections.deleteSection(downStation);

        // then
        assertThat(sections.getSections()).isEmpty();
    }

    @Test
    void 구간_삭제_시_하행_종점역_외_다른역을_삭제하면_예외를_일으킨다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);
        Sections sections = new Sections();
        sections.addSection(section);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                sections.deleteSection(upStation)
        );
    }
}
