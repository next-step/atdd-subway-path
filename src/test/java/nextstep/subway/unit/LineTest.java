package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line 일호선;
    private Station 잠실역;
    private Station 강남역;
    private Station 교대역;

    @BeforeEach
    void init() {
        일호선 = new Line("1호선", "br-red-600");
        잠실역 = new Station("잠실역");
        강남역 = new Station("강남역");
        교대역 = new Station("교대역");
    }

    @DisplayName("지하철 노선의 정보를 업데이트 한다.")
    @Test
    void update() {
        일호선.update("2호선", "green");

        assertThat(일호선).isEqualTo(new Line("2호선", "green"));
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        일호선.addSection(잠실역, 강남역, 10);

        assertThat(일호선.getSections()).containsExactly(new Section(일호선, 잠실역, 강남역, 10));
    }

    @DisplayName("지하철 노선에서 구간을 삭제한다.")
    @Test
    void removeSection() {
        일호선.addSection(잠실역, 강남역, 10);
        일호선.addSection(강남역, 교대역, 10);

        일호선.deleteSection(교대역);

        assertThat(일호선.getSections()).containsExactly(new Section(일호선, 잠실역, 강남역, 10));
    }

    @DisplayName("지하철 노선에 포함돼있는 지하철 역 목록을 조회한다.")
    @Test
    void getStations() {
        일호선.addSection(강남역, 교대역, 10);
        일호선.addSection(교대역, 잠실역, 10);

        List<Section> 구간목록 = 일호선.getSections();

        assertThat(구간목록).containsExactly(new Section(일호선, 강남역, 교대역, 10), new Section(일호선, 교대역, 잠실역, 10));
    }
}
