package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 단위 테스트")
class LineTest {

    @DisplayName("지하철노선의 구간을 추가한다.")
    @Test
    void addSection() {
        // Given
        Line line = new Line();
        List<Section> sections = line.getSections();
        int beforeSize = sections.size();

        // When
        sections.add(new Section());

        // Then
        sections = line.getSections();
        assertThat(sections).hasSize(beforeSize + 1);
    }

    @Test
    void getStations() {
    }

    @DisplayName("지하철노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        // Given
        Station 강남역 = new Station("강남역");
        Station 삼성역 = new Station("삼성역");

        Line line = new Line("2호선", "green");
        List<Section> sections = line.getSections();

        sections.add(new Section(line, 강남역, 삼성역, 10));
        int beforeSize = sections.size();
        int indexOfTailSection = sections.size() - 1;

        // When
        sections.remove(indexOfTailSection);

        // Then
        sections = line.getSections();
        assertThat(sections).hasSize(beforeSize - 1);
    }
}
