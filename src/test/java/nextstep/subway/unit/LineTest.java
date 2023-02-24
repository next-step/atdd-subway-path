package nextstep.subway.unit;

import nextstep.subway.domain.Line;
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

    @DisplayName("지하철역 하행 종점역으로 추가")
    @Test
    void addSection() {
        이호선.addSection(정자역, 광교역, 15);
        assertThat(이호선.getDownStation()).isEqualTo(광교역);
    }

    @DisplayName("지하철 구간 중간에 새로운 지하철역 추가")
    @Test
    void addSection2() {
        이호선.addSection(강남역, 광교역, 6);
        assertThat(이호선.getFirstSection().getDistance()).isEqualTo(6);
        assertThat(이호선.getLastSection().getDistance()).isEqualTo(4);
        assertThat(이호선.getSections()).hasSize(2);
    }

    @DisplayName("상행 종점역으로 추가")
    @Test
    void addSection3() {
        이호선.addSection(정자역, 광교역, 6);
        assertThat(이호선.getUpStation()).isEqualTo(강남역);
    }

    @DisplayName("지하철역 목록 생성")
    @Test
    void getStations() {
        이호선.addSection(정자역, 광교역, 15);
        List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(Arrays.array(강남역, 정자역, 광교역));
    }

    @DisplayName("지하철역 목록 생성, 중간에 추가할 때")
    @Test
    void getStations2() {
        이호선.addSection(강남역, 광교역, 5);
        List<Station> stations = 이호선.getStations();
        assertThat(stations).containsExactly(Arrays.array(강남역, 광교역, 정자역));
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void removeSection() {
        이호선.addSection(정자역, 광교역, 15);
        이호선.removeSection(광교역);
        List<Station> stations = 이호선.getStations();
        assertThat(stations).doesNotContain(광교역);
    }

    @DisplayName("지하철 상행종점역 조회")
    @Test
    void getUpStation() {
        이호선.addSection(정자역, 광교역, 15);
        Station upStation = 이호선.getUpStation();
        assertThat(upStation).isEqualTo(강남역);
    }

    @DisplayName("지하철 하행종점역 조회")
    @Test
    void getDownStation() {
        이호선.addSection(정자역, 광교역, 15);
        Station downStation = 이호선.getDownStation();
        assertThat(downStation).isEqualTo(광교역);
    }
}
