package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Line 신분당선;
    private Station 강남역;
    private Station 판교역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        신분당선 = new Line("신분당선", "green");

        Section section = new Section(신분당선, 강남역, 판교역, 10);
        신분당선.addSection(section);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        assertThat(신분당선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        신분당선.removeSection(판교역);
        assertThat(신분당선.getSections()).isEqualTo(Collections.emptyList());
    }
}
