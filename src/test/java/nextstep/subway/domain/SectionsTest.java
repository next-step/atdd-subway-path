package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    private Sections sections;

    private Line line;
    private Station 중앙역;
    private Station 한대앞역;


    @BeforeEach
    void setUp() {
        sections = new Sections();

        line = new Line("4호선", "blue");
        중앙역 = new Station("중앙역");
        한대앞역 = new Station("한대앞역");

        sections.add(new Section(line, 중앙역, 한대앞역, 10));
    }

    @Test
    @DisplayName("구간이 정상적으로 추가된다.")
    void addTest() {
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 등록된 두 역을 추가하면 추가가 실패한다.")
    void addAlreadyConnectionFailTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(new Section(line, 중앙역, 한대앞역, 10))).withMessage("이미 등록된 역은 등록할 수 없어요.");
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(new Section(line, 한대앞역, 중앙역, 10))).withMessage("이미 등록된 역은 등록할 수 없어요.");
    }

    @Test
    @DisplayName("연결할 수 있는 역이 없으면 구간 추가가 실패한다.")
    void addNonExistConnectStationFailTest() {
        Station 상록수역 = new Station("상록수역");
        Station 반월역 = new Station("반월역");
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(new Section(line, 상록수역, 반월역, 10)))
                .withMessage("연결할 수 있는 역이 없어요.");
    }


    @ParameterizedTest(name = "[{argumentsWithNames}] 상행 종점 또는 하행 종점에 확장시 구간 추가가 된다.")
    @CsvSource(value = {"고잔역:중앙역", "한대앞역:상록수역"}, delimiter = ':')
    void addFirstOrLastSectionTest(String upStationName, String downStationName) {
        sections.add(new Section(line, new Station(upStationName), new Station(downStationName), 10));
        assertThat(sections.size()).isEqualTo(2);
        assertThat(sections.getStations()).containsAnyOf(new Station(upStationName), new Station(downStationName));
    }

    @ParameterizedTest(name = "[{argumentsWithNames}] 역의 중간에 추가하면 할때 기존 구간보다 거리가 짧으면 정상적으로 추가가 된다.")
    @CsvSource(value = {"중앙역:신규역:5", "신규역:한대앞역:5"}, delimiter = ':')
    void addSectionBetweenTest(String upStationName, String downStationName, int distance) {
        sections.add(new Section(line, new Station(upStationName), new Station(downStationName), distance));
        assertThat(sections.size()).isEqualTo(2);
        assertThat(sections.getStations()).containsExactly(new Station("중앙역"), new Station("신규역"), new Station("한대앞역"));
    }

    @ParameterizedTest(name = "[{argumentsWithNames}] 역의 중간에 추가하면 할때 기존 구간보다 거리가 같거나 길면 추가가 실패한다.")
    @CsvSource(value = {"중앙역:신규역:10", "중앙역:신규역:11", "신규역:한대앞역:10", "신규역:한대앞역:11"}, delimiter = ':')
    void addSectionBetweenFailTest(String upStationName, String downStationName, int distance) {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.add(new Section(line, new Station(upStationName), new Station(downStationName), distance)));
    }

    @Test
    @DisplayName("구간이 정상적으로 조회된다.")
    void getStationsTest() {
        assertThat(sections.getStations()).containsExactly(중앙역, 한대앞역);
    }


    @Test
    @DisplayName("제거할 역이 상행 종점인 경우 제거된다.")
    void deleteStationEndOfUpStationTest() {
        Station 상행종점역 = new Station("상행종점역");
        sections.add(new Section(line, 상행종점역, 중앙역, 10));
        assertThat(sections.size()).isEqualTo(2);

        sections.deleteStation(상행종점역);

        assertAll(
                () -> assertThat(sections.getStations()).hasSize(2),
                () -> assertThat(sections.getStations()).doesNotContain(상행종점역),
                () -> assertThat(sections.getStations()).containsExactly(중앙역, 한대앞역)
        );
    }


    @Test
    @DisplayName("제거할 역이 하행종점역 경우 제거된다.")
    void deleteStationTest() {
        Station 하행종점역 = new Station("하행종점역");
        sections.add(new Section(line, 한대앞역, 하행종점역, 10));
        assertThat(sections.size()).isEqualTo(2);

        sections.deleteStation(하행종점역);

        assertAll(
                () -> assertThat(sections.getStations()).hasSize(2),
                () -> assertThat(sections.getStations()).doesNotContain(하행종점역),
                () -> assertThat(sections.getStations()).containsExactly(중앙역, 한대앞역)
        );
    }

    @Test
    @DisplayName("구간 중간 역을 제거하면 제거된고 거리가 합쳐진다.")
    void deleteStationMiddleTest() {
        Station 중간역 = new Station("중간역");
        sections.add(new Section(line, 중앙역, 중간역, 4));
        assertThat(sections.size()).isEqualTo(2);

        sections.deleteStation(중간역);

        assertAll(
                () -> assertThat(sections.getStations()).hasSize(2),
                () -> assertThat(sections.getStations()).doesNotContain(중간역),
                () -> assertThat(sections.getStations()).containsExactly(중앙역, 한대앞역),
                () -> assertThat(sections.size()).isEqualTo(1),
                () -> assertThat(sections.get(0).getDistance()).isEqualTo(10)
        );
    }


    @Test
    @DisplayName("구간이 2개 미만일때 삭제시 에러가 발생한다.")
    void deleteStationMinSizeFailTest() {
        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(중앙역))
                .withMessage("구간은 두개 이상부터 제거가 가능해요");
    }

    @Test
    @DisplayName("제거할 역이 없는 역이면 에러가 발생한다.")
    void deleteStationFailTest() {
        Station 상록수역 = new Station("상록수역");
        sections.add(new Section(line, 한대앞역, 상록수역, 10));

        assertThatIllegalArgumentException().isThrownBy(() -> sections.deleteStation(new Station("없는역")))
                .withMessage("삭제할 역이 없어요");
    }


    // TODO: 구간이 합쳐지는 테스트  2022/07/22 (koi) 및


    /**
     * deleteStation
     */
}