package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineErrorMessage;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    private Station upStation;
    private Station middleStation;
    private Station downStation;

    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("상행역");
        middleStation = new Station("중간역");
        downStation = new Station("하행역");

        line = new Line("1호선", "파란색");
        line.addSection(upStation, downStation, 10);
    }

    @Test
    void addSection() {
        // given
        Line line = new Line("1호선", "파란색");

        // when
        line.addSection(upStation, downStation, 10);

        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    @Test
    void addSectionInExistentSection() {
        // when
        line.addSection(upStation, middleStation, 6);

        // then
        assertThat(line.getOrderedStations()).extracting("name").containsExactly("상행역", "중간역", "하행역");
        assertThat(line.getSections()).extracting("distance").containsExactly(6, 4);
    }

    @Test
    void addSectionWithTooLongDistance() {
        // when & then
        assertThatThrownBy(() -> line.addSection(upStation, middleStation, 10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(LineErrorMessage.ADD_SECTION_INVALID_DISTANCE.getMessage());
    }

    @Test
    void addAlreadyExistentStations() {
        // when & then
        assertThatThrownBy(() -> line.addSection(upStation, downStation, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(LineErrorMessage.ADD_SECTION_STATIONS_ALREADY_EXIST.getMessage());
    }

    @Test
    void addNonExistentStations() {
        // given
        Station upStation = new Station("공릉역");
        Station downStation = new Station("소요산역");

        // when & then
        assertThatThrownBy(() -> line.addSection(upStation, downStation, 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(LineErrorMessage.ADD_SECTION_STATIONS_NOT_EXIST.getMessage());
    }

    @Test
    void getStations() {
        // when & then
        assertThat(line.getOrderedStations()).extracting("name").containsExactly("상행역", "하행역");
    }

    @Test
    void removeSection() {
        // when
        line.removeSection(line.getOrderedStations().get(1));

        // then
        assertThat(line.getSections()).isEmpty();
    }

    @Test
    void removeNonLastSection() {
        // when & then
        assertThatThrownBy(() -> line.removeSection(line.getOrderedStations().get(0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateLine() {
        // when
        line.updateLine("2호선", "초록색");

        // then
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("초록색");
    }
}
