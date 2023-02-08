package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    Line 신분당선;
    Station 신논현역;
    Station 강남역;
    Station 양재역;
    Section 신논현_강남_구간;
    Section 강남_양재_구간;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "RED");
        신논현역 = new Station("신논현역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        신논현_강남_구간 = new Section(신분당선, 신논현역, 강남역, 10);
        강남_양재_구간 = new Section(신분당선, 강남역, 양재역, 5);
    }

    @Test
    @DisplayName("지하철 구간 추가")
    void addSection() {
        // Given
        // When
        신분당선.addSection(신논현_강남_구간);

        // Then
        List<Section> sections = 신분당선.getSections();
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(1),
                () -> assertThat(sections.get(0).getUpStation().getName()).isEqualTo("신논현역"),
                () -> assertThat(sections.get(0).getDownStation().getName()).isEqualTo("강남역"),
                () -> assertThat(sections.get(0).getDistance()).isEqualTo(10)
        );
    }

    @Test
    @DisplayName("지하철역 목록 조회")
    void getStations() {
        // Given
        신분당선.addSection(신논현_강남_구간);
        신분당선.addSection(강남_양재_구간);

        // When
        List<Station> stations = 신분당선.getStations();

        // Then
        assertThat(stations).extracting("name").containsExactly("신논현역", "강남역", "양재역");
    }

    @Test
    @DisplayName("지하철 구간 삭제")
    void removeSection() {
        // Given
        신분당선.addSection(신논현_강남_구간);
        신분당선.addSection(강남_양재_구간);

        // When
        신분당선.removeSection(양재역);

        // Then
        List<Section> sections = 신분당선.getSections();
        assertThat(sections.size()).isEqualTo(1);

        List<Station> stations = 신분당선.getStations();
        assertThat(stations).extracting("name").containsExactly("신논현역", "강남역");
    }
}
