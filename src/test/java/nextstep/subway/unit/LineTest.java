package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        sut.addSection(광교역, 광교중앙중앙역, 10);

        assertThat(sut.getStations()).containsExactly(광교역, 광교중앙중앙역, 광교중앙역);
    }

    @DisplayName("구간 사이에 새 구간 추가 (상행역이 신규역)")
    @Test
    void addSectionWithNewUpStationInMiddle() {
        var 광교중앙중앙역 = new Station("광교중앙중앙역");

        sut.addSection(광교중앙중앙역, 광교중앙역, 10);

        assertThat(sut.getStations()).containsExactly(광교역, 광교중앙중앙역, 광교중앙역);
    }

    @DisplayName("구간의 상하행역이 모두 노선에 존재하지 않으면 추가 실패")
    @Test
    void sectionAdditionFailsWhenNeitherUpAndDownStationNotExist() {
        var 새로운역 = new Station("새로운역");
        var 다른새로운역 = new Station("다른새로운역");

        assertThrows(IllegalArgumentException.class, () -> sut.addSection(새로운역, 다른새로운역, 10));
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
