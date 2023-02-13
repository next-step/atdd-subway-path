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
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        이호선 = new Line("2호선", "green");
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // when
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 3));

        // then
        assertThat(이호선.getSections()).containsExactly(new Section(이호선, 강남역, 역삼역, 3));
    }

    @DisplayName("지하철 노선에 등록된 역들을 조회한다.")
    @Test
    void getStations() {
        // given
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 3));

        // when & then
        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("지하철 노선에 등록된 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 3));

        // when
        이호선.removeSection(역삼역);

        // then
        assertThat(이호선.getStations()).isEmpty();
    }
}
