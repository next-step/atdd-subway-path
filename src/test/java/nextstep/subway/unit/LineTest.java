package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 익명역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        익명역 = new Station(4L, "익명역");
        이호선 = new Line("이호선", "Green", 10, 강남역, 역삼역);
    }


    @DisplayName("구간을 등록한다. 기존 구간 A-C에 신규 구간 C-D 추가")
    @Test
    void addSectionACCD() {
        // when
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 10));

        // then
        assertThat(이호선.getStations()).contains(선릉역);
    }

    @DisplayName("구간을 등록한다. 기존 구간 A-C에 신규 구간 A-B 추가")
    @Test
    void addSectionACAB() {
        // when
        int smallerDistanceThanSectionAC = 이호선.getDistance() - 1;
        이호선.addSection(new Section(이호선, 강남역, 익명역, smallerDistanceThanSectionAC));

        // then
        assertThat(이호선.getStations()).contains(익명역);
    }

    @DisplayName("구간을 등록한다. 기존 구간 A-C에 신규 구간 B-A 추가")
    @Test
    void addSectionACBA() {
        // when
        이호선.addSection(new Section(이호선, 익명역, 강남역, 10));

        // then
        assertThat(이호선.getStations()).contains(익명역);
    }

    @DisplayName("모든 지하철 조회")
    @Test
    void getStations() {
        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역);
    }

    @DisplayName("모든 지하철 조회 시, 상행종착역-하행종착역 순으로 반환")
    @Test
    void checkGetStationsReturnInOrder() {
        // given
        int smallerDistanceThanSectionAC = 이호선.getDistance() - 1;
        이호선.addSection(new Section(이호선, 강남역, 익명역, smallerDistanceThanSectionAC));

        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 익명역, 역삼역);
    }

    @DisplayName("지하철 삭제")
    @Test
    void removeSection() {
        // given
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 10));

        // when
        이호선.removeSection(선릉역);

        // then
        assertThat(이호선.getStations()).doesNotContain(선릉역);
    }
}
