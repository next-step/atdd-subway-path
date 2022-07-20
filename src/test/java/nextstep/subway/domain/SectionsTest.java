package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class SectionsTest {

    private Sections sections;

    private Line line;
    private Station station1;
    private Station station2;


    @BeforeEach
    void setUp() {
        sections = new Sections();

        line = new Line("4호선", "blue");
        station1 = new Station("중앙역");
        station2 = new Station("한대앞역");

        sections.add(new Section(line, station1, station2, 10));
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addTest() {
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 등록된 구간은 추가가 실패한다.")
    void addAlreadyConnectionFailTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(new Section(line, station1, station2, 10)))
                .withMessage("이미 등록된 역은 등록할 수 없어요.");
    }

    @Test
    @DisplayName("연결할 수 있는 역이 없으면 구간 추가가 실패한다.")
    void addNonExistConnectStationFailTest() {
        Station station3 = new Station("상록수역");
        Station station4 = new Station("반월역");
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(new Section(line, station3, station4, 10)))
                .withMessage("연결할 수 있는 역이 없어요.");
    }


    @ParameterizedTest(name = "[{argumentsWithNames}] 상행 종점 또는 하행 종점에 확장시 구간 추가가 된다.")
    @CsvSource(value = {"고잔역:중앙역", "한대앞역:상록수역"}, delimiter = ':')
    void addFirstOrLastSectionTest(String upStationName, String downStationName) {
        sections.add(new Section(line, new Station(upStationName), new Station(downStationName), 10));
        assertThat(sections.size()).isEqualTo(2);
    }

    @ParameterizedTest( name = "[{argumentsWithNames}] 역의 중간에 추가하면 할때 기존 구간보다 거리가 짧으면 정상적으로 추가가 된다.")
    @CsvSource(value = {"중앙역:신규역:5", "신규역:한대앞역:5"}, delimiter = ':')
    void addSectionBetweenTest(String upStationName, String downStationName, int distance) {
        sections.add(new Section(line, new Station(upStationName), new Station(downStationName), distance));
        assertThat(sections.size()).isEqualTo(2);
        assertThat(sections.getStations()).containsExactly(new Station("중앙역"), new Station("신규역"), new Station("한대앞역"));
    }

    @ParameterizedTest( name = "[{argumentsWithNames}] 역의 중간에 추가하면 할때 기존 구간보다 거리가 같거나 길면 추가가 실패한다.")
    @CsvSource(value = {"중앙역:신규역:10", "중앙역:신규역:11", "신규역:한대앞역:10", "신규역:한대앞역:11"}, delimiter = ':')
    void addSectionBetweenFailTest(String upStationName, String downStationName, int distance) {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(new Section(line, new Station(upStationName), new Station(downStationName), distance)));
    }

    @Test
    @DisplayName("구간이 정상적으로 조회된다.")
    void getStationsTest() {
        assertThat(sections.getStations()).containsExactly(station1, station2);
    }

    @Test
    @DisplayName("제거할 역이 마지막 역인 경우 제거된다.")
    void deleteStationTest() {
        Station station3 = new Station("상록수역");
        sections.add(new Section(line, station2, station3, 10));

        assertThat(sections.size()).isEqualTo(2);

        sections.deleteStation(station3);
        assertThat(sections.getStations()).hasSize(2);
        assertThat(sections.getStations()).doesNotContain(station3);
    }

    @Test
    @DisplayName("제거할 역이 마지막 역이 아닌경우 에러가 발생한다.")
    void deleteStationFailTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(station1));
        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(new Station("없는역")));
    }
}