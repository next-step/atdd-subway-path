package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Sections sut;

    private Line 분당선;
    private Station 청량리역;
    private Station 왕십리역;

    @BeforeEach
    void setUp() {
        분당선 = new Line("분당선", "yellow");
        청량리역 = new Station("청량리역");
        왕십리역 = new Station("왕십리역");
        sut = new Sections();
        sut.add(분당선, 청량리역, 왕십리역, 10);
    }

    @DisplayName("마지막 구간 추가")
    @Test
    void addLastSection() {
        var 서울숲역 = new Station("서울숲역");
        sut.add(분당선, 왕십리역, 서울숲역, 10);

        assertThat(sut.getStations()).containsExactly(청량리역, 왕십리역, 서울숲역);
    }

    @DisplayName("첫 구간 추가")
    @Test
    void addFirstSection() {
        var 새로운역 = new Station("새로운역");
        sut.add(분당선, 새로운역, 청량리역, 10);

        assertThat(sut.getStations()).containsExactly(새로운역, 청량리역, 왕십리역);
    }

    @DisplayName("구간 제거")
    @Test
    void removeSection() {
        sut.removeByStation(왕십리역);

        assertThat(sut.getStations()).isEmpty();
    }

    @DisplayName("마지막 역이 아닌 역으로 구간 제거시 예외 발생")
    @Test
    void cantRemoveSectionByStationInMiddle() {
        assertThrows(IllegalArgumentException.class, () -> sut.removeByStation(청량리역));
    }

}