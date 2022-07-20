package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    @Test
    void 구간을_추가한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        line.addSection(section);

        // then
        assertAll(() -> {
            assertThat(line.getSections()).hasSize(1);
            assertThat(line.getSections()).containsExactly(section);
        });
    }

    @Test
    void 노선_정보를_수정하라() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.changeInfo("8호선", "pink");

        // then
        assertAll(() -> {
            assertThat(line.getName()).isEqualTo("8호선");
            assertThat(line.getColor()).isEqualTo("pink");
        });
    }

    @Test
    void 노선_정보를_수정_시_이름이_null_인경우_이름은_변경되지_않는다() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.changeInfo(null, "pink");

        // then
        assertAll(() -> {
            assertThat(line.getName()).isEqualTo("2호선");
            assertThat(line.getColor()).isEqualTo("pink");
        });
    }

    @Test
    void 노선_정보를_수정_시_색상이_null_인경우_색상은_변경되지_않는다() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.changeInfo("8호선", null);

        // then
        assertAll(() -> {
            assertThat(line.getName()).isEqualTo("8호선");
            assertThat(line.getColor()).isEqualTo("green");
        });
    }

    @Test
    void 노선_정보를_수정_시_이름과_색상이_null_인경우_둘다_변경되지_않는다() {
        // given
        Line line = new Line("2호선", "green");

        // when
        line.changeInfo(null, null);

        // then
        assertAll(() -> {
            assertThat(line.getName()).isEqualTo("2호선");
            assertThat(line.getColor()).isEqualTo("green");
        });
    }

    @Test
    void 노선에_등록된_역을_조회한다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);

        // when
        List<Station> stations = section.getStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @Test
    void 노선_정보를_삭제한다() {
        // given
        Line line = new Line("2호선", "green");
        Station station1 = new Station("숭실대역");
        Station station2 = new Station("모란역");
        Station station3 = new Station("암사역");

        Section section1 = new Section(line, station1, station2, 10);
        Section section2 = new Section(line, station1, station3, 4);
        line.addSection(section1);
        line.addSection(section2);

        // when
        line.deleteSection(station1);

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @Test
    void 노선_정보를_삭제_시_하행_종점역_외_다른역을_삭제하면_예외를_일으킨다() {
        // given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("암사역");
        Station downStation = new Station("모란역");
        Section section = new Section(line, upStation, downStation, 10);
        line.addSection(section);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                line.deleteSection(upStation)
        );
    }
}
