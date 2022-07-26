package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
        // Given
        Section 서울대입구_낙성대_구간 = new Section(이호선, 서울대입구역, 낙성대역, 10);

        // When
        이호선.addSection(서울대입구_낙성대_구간);

        // Then
        assertThat(이호선.getSectionList()).contains(서울대입구_낙성대_구간);
    }

    @DisplayName("지하철 노선에 포함된 역을 조회합니다.")
    @Test
    void getStations() {
        // Given
        Section 서울대입구_낙성대_구간 = new Section(이호선, 서울대입구역, 낙성대역, 10);
        이호선.addSection(서울대입구_낙성대_구간);

        // When
        List<Station> stations = 이호선.getStations();

        // Then
        assertThat(stations).contains(서울대입구역, 낙성대역);
    }

    @DisplayName("지하철 노선에 포함된 구간을 삭제합니다.")
    @Test
    void removeSection() {
        // Given
        Section 서울대입구_낙성대_구간 = new Section(이호선, 서울대입구역, 낙성대역, 10);
        이호선.addSection(서울대입구_낙성대_구간);

        // When
        이호선.deleteStation(낙성대역);

        // Then
        assertThat(이호선.isEmptySections()).isTrue();
    }
}
