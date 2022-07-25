package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.NotValidSectionDistanceException;
import nextstep.subway.domain.exception.NotValidSectionStationsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LineTest {

    private Station 광교역 = new Station("광교역");
    private Station 광교중앙역 = new Station("광교중앙역");

    private Line sut;

    // Given: 구간 하나를 포함한 노선 추가
    @BeforeEach
    void setUp() {
        sut = new Line();
        sut.addSection(광교역, 광교중앙역, 10);
    }

    @DisplayName("마지막 구간 추가")
    @Test
    void addLastSection() {
        var 상현역 = new Station("상현역");

        sut.addSection(광교중앙역, 상현역, 10);

        assertThat(sut.getStations()).containsExactly(광교역, 광교중앙역, 상현역);
    }

    @DisplayName("첫 구간 추가")
    @Test
    void addFirstSection() {
        var 호매실역 = new Station("호매실역");

        sut.addSection(호매실역, 광교역, 10);

        assertThat(sut.getStations()).containsExactly(호매실역, 광교역, 광교중앙역);
    }

    @DisplayName("구간 사이에 새 구간 추가 (하행역이 신규역)")
    @Test
    void addSectionWithNewDownStationInMiddle() {
        var 광교중앙중앙역 = new Station("광교중앙중앙역");

        sut.addSection(광교역, 광교중앙중앙역, 5);

        assertThat(sut.getStations()).containsExactly(광교역, 광교중앙중앙역, 광교중앙역);
    }

    @DisplayName("구간 사이에 새 구간 추가 (상행역이 신규역)")
    @Test
    void addSectionWithNewUpStationInMiddle() {
        var 광교중앙중앙역 = new Station("광교중앙중앙역");

        sut.addSection(광교중앙중앙역, 광교중앙역, 5);

        assertThat(sut.getStations()).containsExactly(광교역, 광교중앙중앙역, 광교중앙역);
    }

    @ParameterizedTest(name = "구간 사이의 새 구간의 거리가 기존 구간보다 크거나 같으면 추가 실패 / distance = {0}")
    @ValueSource(ints = {10, 15})
    void sectionAdditionFailsWhenDistanceOfNewSectionInMiddleIsGreater(int distance) {
        var 광교중앙중앙역 = new Station("중간역");

        assertThrows(NotValidSectionDistanceException.class, () -> sut.addSection(광교역, 광교중앙중앙역, distance));
    }

    @DisplayName("구간의 상하행역이 모두 노선에 존재하지 않으면 추가 실패")
    @Test
    void sectionAdditionFailsWhenNeitherUpAndDownStationNotExist() {
        var 새로운역 = new Station("새로운역");
        var 다른새로운역 = new Station("다른새로운역");

        assertThrows(NotValidSectionStationsException.class, () -> sut.addSection(새로운역, 다른새로운역, 10));
    }

    @DisplayName("노선 내 역 조회")
    @Test
    void getStations() {
        var 상현역 = new Station("상현역");
        sut.addSection(광교중앙역, 상현역, 10);

        var stations = sut.getStations();

        assertThat(stations).containsExactly(광교역, 광교중앙역, 상현역);
    }

    @DisplayName("노선 내 구간 삭제")
    @Test
    void removeSection() {
        var 상현역 = new Station("상현역");
        sut.addSection(광교중앙역, 상현역, 10);

        sut.removeSection(상현역);

        assertThat(sut.getStations()).containsExactly(광교역, 광교중앙역);
    }
}
