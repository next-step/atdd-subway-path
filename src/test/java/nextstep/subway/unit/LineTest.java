package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    private Line 이호선;
    private List<Section> sections;
    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;

    @BeforeEach
    void setUp() {
        // Given
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 강남역, 삼성역, 10));

        sections = 이호선.getSections();
    }

    @DisplayName("지하철노선의 구간을 추가한다.")
    @Test
    void addSection() {
        // Given
        int beforeSize = sections.size();

        // When
        이호선.addSection(new Section(이호선, 삼성역, 잠실역, 7));

        // Then
        sections = 이호선.getSections();
        assertThat(sections).hasSize(beforeSize + 1);
    }

    @DisplayName("지하철노선의 역을 조회한다.")
    @Test
    void getStations() {
        // When
        List<Station> stations = new ArrayList<>();

        List<Section> foundSections = 이호선.getSections();
        foundSections.forEach(section -> {
            stations.add(section.getUpStation());
            if (foundSections.indexOf(section) == foundSections.size() - 1) {
                stations.add(section.getDownStation());
            }
        });

        // Then
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 삼성역)
        );
    }

    @DisplayName("지하철노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        // Given
        int beforeSize = sections.size();
        int indexOfTailSection = sections.size() - 1;

        // When
        sections.remove(indexOfTailSection);

        // Then
        sections = 이호선.getSections();
        assertThat(sections).hasSize(beforeSize - 1);
    }
}
