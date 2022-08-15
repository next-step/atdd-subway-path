package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        이호선 = new Line("2호선", "green");
    }

    @DisplayName("노선에 연결되어있는 정렬된 지하철 노선 목록 조회")
    @Test
    void getStations() {
        // given
        이호선.addSection(new Section(이호선, 강남역, 삼성역, 10));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 9));

        // when
        final Stations 이호선_역_목록 = 이호선.getStations();

        // then
        assertThat(이호선_역_목록.getList()).containsExactly(강남역, 역삼역, 삼성역);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void update() {
        // when
        이호선.update("신분당선", "red");
        // then
        assertThat(이호선.getName()).isEqualTo("신분당선");
        assertThat(이호선.getColor()).isEqualTo("red");
    }
}
