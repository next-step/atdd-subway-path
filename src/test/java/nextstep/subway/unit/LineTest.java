package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 강남역;
    private Station 정자역;
    private Station 광교역;

    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        정자역 = new Station("정자역");
        광교역 = new Station("광교역");
        이호선 = new Line("2호선", "green", 강남역, 정자역, 10);
    }
    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        이호선.addSection(정자역, 광교역, 15);
        List<Section> sections = 이호선.getSections();
        assertThat(sections.get(1).getDownStation()).isEqualTo(광교역);
    }

    @DisplayName("지하철역 목록 생성")
    @Test
    void getStations() {
        이호선.addSection(정자역, 광교역, 15);
        List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(Arrays.array(강남역, 정자역, 광교역));
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void removeSection() {
        이호선.addSection(정자역, 광교역, 15);
        이호선.removeSection();
        List<Station> stations = 이호선.getStations();
        assertThat(stations).doesNotContain(광교역);
    }
}
