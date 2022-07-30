package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line 이호선;
    private Station 서울대입구역;
    private Station 낙성대역;
    private Station 사당역;

    @BeforeEach
    void init() {
        이호선 = new Line("이호선", "br-red-600");
        서울대입구역 = new Station("서울대입구역");
        낙성대역 = new Station("낙성대역");
        사당역 = new Station("사당역");
    }

    @DisplayName("지하철 노선에 구간을 추가합니다.")
    @Test
    void addSection() {
        // Given & When
        이호선.addSection(이호선, 서울대입구역, 낙성대역, 10);

        // Then
        assertThat(이호선.getSectionList()
                .size()).isEqualTo(1);
        assertThat(이호선.getStations()).contains(서울대입구역, 낙성대역);
    }

    @DisplayName("지하철 노선에 포함된 역을 조회합니다.")
    @Test
    void getStations() {
        // Given
        이호선.addSection(이호선, 서울대입구역, 낙성대역, 10);

        // When
        List<Station> stations = 이호선.getStations();

        // Then
        assertThat(stations).contains(서울대입구역, 낙성대역);
    }

    @DisplayName("지하철 노선에 포함된 구간을 삭제합니다.")
    @Test
    void removeSection() {
        // Given
        이호선.addSection(이호선, 서울대입구역, 낙성대역, 10);
        이호선.addSection(이호선, 낙성대역, 사당역, 10);

        // When
        이호선.deleteStation(사당역);

        // Then
        assertThat(이호선.getStations()).containsExactly(서울대입구역, 낙성대역);
    }
}
