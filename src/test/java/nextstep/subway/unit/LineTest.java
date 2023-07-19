package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import org.assertj.core.api.Assertions;
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
    Station 판교역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        익명역 = new Station(4L, "익명역");
        판교역 = new Station(5L, "판교역");
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

    @DisplayName("구간을 등록한다. 기존 구간 A-C에 신규 구간 B-C 추가")
    @Test
    void addSectionACBC() {
        // when
        int smallerDistanceThanSectionAC = 이호선.getDistance() - 1;
        이호선.addSection(new Section(이호선, 익명역, 역삼역, smallerDistanceThanSectionAC));

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

    @DisplayName("구간을 등록 실패. 기존 구간 A-C 보다 신규 구간 B-C 길이가 크거나 같음")
    @Test
    void stationRegistrationBetweenStationsFailBySameOrBiggerDistance() {
        // when
        int sameDistanceComparedToSectionAC = 이호선.getDistance();
        Assertions.assertThatThrownBy(() -> 이호선.addSection(new Section(이호선, 익명역, 역삼역, sameDistanceComparedToSectionAC)))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        assertThat(이호선.getStations()).doesNotContain(익명역);
    }

    @DisplayName("구간 등록 실패, 상행역과 하행역이 이미 모두 노선에 등록돼있는 구간을 추가")
    @Test
    void stationRegistrationFailByAlreadyExistingTopStationAndDownStation() {
        // given
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 10));

        // when
        Assertions.assertThatThrownBy(() -> 이호선.addSection(new Section(이호선, 강남역, 역삼역, 10)))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThatThrownBy(() -> 이호선.addSection(new Section(이호선, 역삼역, 선릉역, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간 등록 실패, 상행역과 하행역이 모두 노선에 포함되있지 않은 구간을 추가")
    @Test
    void stationRegistrationFailByLineDoNotContainSectionRelatedStations() {
        // given
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 10));

        // when
        Assertions.assertThatThrownBy(() -> 이호선.addSection(new Section(이호선, 익명역, 판교역, 10)))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        assertThat(이호선.getStations()).doesNotContain(익명역);
        assertThat(이호선.getStations()).doesNotContain(판교역);
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
