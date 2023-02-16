package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 테스트")
class LineTest {
    @Test
    @DisplayName("지하철 2호선에 강남역 - 역삼역 구간을 추가할 수 있다")
    void addSection() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section section = new Section(line, upStation, downStation, 10);

        // When
        line.addSection(section);

        // Then
        assertThat(line.getSections())
            .isNotEmpty()
            .contains(section);
    }

    @Test
    @DisplayName("지하철 2호선에 포함된 역들을 조회할 수 있다")
    void getStations() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Section section1 = new Section(line, station1, station2, 10);

        Station station3 = new Station("선릉역");
        Section section2 = new Section(line, station2, station3, 20);

        line.addSection(section1);
        line.addSection(section2);

        // When
        List<Station> stations = line.getStations();

        // Then
        assertThat(stations)
            .isNotEmpty()
            .hasSize(3)
            .containsOnly(station1, station2, station3);
    }

    @Test
    @DisplayName("지하철 2호선에 마지막 구간을 제거할 수 있다")
    void removeSection() {
        // Given
        Line line = new Line("2호선", "bg-red-006");
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Section section1 = new Section(line, station1, station2, 10);

        Station station3 = new Station("선릉역");
        Section section2 = new Section(line, station2, station3, 20);

        line.addSection(section1);
        line.addSection(section2);

        // When
        line.removeSection(station3);

        // Then
        assertThat(line.getSections())
            .isNotEmpty()
            .hasSize(1);

        assertThat(line.getSections())
            .containsOnly(section1);

        assertThat(line.getStations())
            .isNotEmpty()
            .containsOnly(station1, station2);
    }
}
