package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private Station 강남역;
    private Station 역삼역;
    private Line line;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        line = new Line("2호선", "green");
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // when
        line.addSection(new Section(line, 강남역, 역삼역, 3));

        // then
        assertThat(line.getSections()).containsExactly(new Section(line, 강남역, 역삼역, 3));
    }

    @DisplayName("지하철 노선에 등록된 역들을 조회한다.")
    @Test
    void getStations() {
        // given
        line.addSection(new Section(line, 강남역, 역삼역, 3));

        // when & then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("지하철 노선에 등록된 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        line.addSection(new Section(line, 강남역, 역삼역, 3));

        // when
        line.removeSection(역삼역);

        // then
        assertThat(line.getStations()).isEmpty();
    }
}
