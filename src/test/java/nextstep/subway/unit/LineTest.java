package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

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
