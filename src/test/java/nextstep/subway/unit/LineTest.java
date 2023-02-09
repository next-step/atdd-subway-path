package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineErrorMessage;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    void addSection() {
        // given
        Line line = new Line("1호선", "파란색");

        // when
        line.addSection(createUpStation(), createDownStation(), 10);

        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void addSectionInExistentSection() {
        // given
        Line line = createLine();

        // when
        line.addSection(createUpStation(), createMiddleStation(), 6);

        // then
        assertThat(line.getOrderedStations()).extracting("name").containsExactly("상행역", "중간역", "하행역");
        assertThat(line.getSections()).extracting("distance").containsExactly(6, 4);
    }

    @Test
    void addSectionWithTooLongDistance() {
        // given
        Line line = createLine();

        // when & then
        assertThatThrownBy(() -> line.addSection(createUpStation(), createMiddleStation(), 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(LineErrorMessage.INVALID_DISTANCE.getMessage());
    }

    @Test
    void addAlreadyExistentStations() {
        // given
        Line line = createLine();
        Station upStation = createUpStation();
        Station downStation = createDownStation();

        // when & then
        assertThatThrownBy(() -> line.addSection(upStation, downStation, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(LineErrorMessage.STATIONS_ALREADY_EXIST.getMessage());
    }

    @Test
    void addNonExistentStations() {
        // given
        Line line = createLine();
        Station upStation = new Station("공릉역");
        Station downStation = new Station("소요산역");

        // when & then
        assertThatThrownBy(() -> line.addSection(upStation, downStation, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(LineErrorMessage.STATIONS_NOT_EXIST.getMessage());
    }

    @Test
    void getStations() {
        // given
        Line line = createLine();

        // when & then
        assertThat(line.getOrderedStations()).extracting("name").containsExactly("상행역", "하행역");
    }

    @Test
    void removeSection() {
        // given
        Line line = createLine();

        // when
        line.removeSection(line.getOrderedStations().get(1));

        // then
        assertThat(line.getSections()).isEmpty();
    }

    @Test
    void removeNonLastSection() {
        // given
        Line line = createLine();

        // when & then
        assertThatThrownBy(() -> line.removeSection(line.getOrderedStations().get(0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateLine() {
        // given
        Line line = createLine();

        // when
        line.updateLine("2호선", "초록색");

        // then
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("초록색");
    }

    private static Line createLine() {
        Line line = new Line("1호선", "파란색");
        line.addSection(createUpStation(), createDownStation(), 10);
        return line;
    }

    private static Station createUpStation() {
        return new Station("상행역");
    }

    private static Station createDownStation() {
        return new Station("하행역");
    }

    private static Station createMiddleStation() {
        return new Station("중간역");
    }
}
