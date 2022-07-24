package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Line 신분당선;
    Station 신논현역;
    Station 논현역;
    Station 강남역;
    Station 양재역;

    @BeforeEach
    void setUp() {
        신분당선 = Line.of("신분당선", "RED");
        신논현역 = new Station("신논현역");
        논현역 = new Station("논현역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
    }

    @DisplayName("구간 등록 성공")
    @Test
    void addSection() {
        신분당선.addSection(논현역, 신논현역, 5);
        assertThat(신분당선.sections().size()).isEqualTo(1);
        assertThat(신분당선.upStation().getName()).isEqualTo("논현역");
        assertThat(신분당선.downStation().getName()).isEqualTo("신논현역");
    }

    @DisplayName("역 목록 조회")
    @Test
    void getStations() {
        신분당선.addSection(논현역, 신논현역, 5);
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(강남역, 양재역, 5);
        assertThat(신분당선.stations()).containsExactly(논현역, 신논현역, 강남역, 양재역);
    }

    @DisplayName("구간 사이에 구간 추가")
    @Test
    void addSection2() {
        신분당선.addSection(논현역, 강남역, 5);
        신분당선.addSection(논현역, 신논현역, 3);
        assertThat(신분당선.stations()).containsExactly(논현역, 신논현역, 강남역);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void addUpStation() {
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(논현역, 신논현역, 3);
        assertThat(신분당선.stations()).containsExactly(논현역, 신논현역, 강남역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void addDownStation() {
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.addSection(강남역, 양재역, 3);
        assertThat(신분당선.stations()).containsExactly(신논현역, 강남역, 양재역);
    }

    @DisplayName("구간 삭제")
    @Test
    void removeSection() {
        신분당선.addSection(논현역, 신논현역, 5);
        신분당선.addSection(신논현역, 강남역, 5);
        신분당선.deleteSection(강남역);
        assertThat(신분당선.sections().size()).isEqualTo(1);
    }
}
