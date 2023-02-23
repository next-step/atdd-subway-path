package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 테스트")
class LineTest {

    private Line 신분당선;

    private Sections sections;

    private Station 강남역;

    private Station 양재역;

    private Station 판교역;

    private Station 정자역;

    @BeforeEach
    void setUp() {
        // Given - 초기값 셋팅
        강남역 = new Station(1L,"강남역");
        양재역 = new Station(2L,"양재역");
        판교역 = new Station(3L,"판교역");
        정자역 = new Station(4L,"정자역");
        신분당선 = new Line("신분당선", "bg-red-600");
    }

    @DisplayName("지하철 구간 추가 테스트")
    @Test
    void addSection() {
        // Given

        // When
        신분당선.addSection(판교역, 정자역, 10);

        // Then
        sections = 신분당선.getSections();
        assertThat(sections.getStations()).hasSize(2);
    }

    @DisplayName("지하철 역 목록 조회")
    @Test
    void getStations() {
        // Given
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 판교역, 5);

        // When
        List<Station> stations = 신분당선.getStations();

        // Then
        assertThat(stations).extracting("name").containsExactly("강남역", "양재역", "판교역");
    }

    @DisplayName("지하철 구간 삭제 테스트")
    @Test
    void removeSection() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 판교역, 5);

        // when
        신분당선.deleteSection(판교역.getId());

        // then
        assertThat(신분당선.getStations()).extracting("name").containsExactly("강남역", "양재역");
        assertThat(신분당선.getSections().getSections()).hasSize(1);
    }
}
