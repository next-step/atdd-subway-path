package nextstep.subway.unit.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SectionTest {
    private Line 이호선;
    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;
    private Section section;

    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "green");
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        section = new Section(이호선, 강남역, 삼성역, 10);

        잠실역 = new Station("잠실역");
    }

    /**
     * criteria
     *                  강남역 <---------10---------> 삼성역
     *
     * target
     * 잠실역 <----6----> 강남역
     */
    @DisplayName("구간의 상행역과 대상의 하행역이 일치한다")
    @Test
    void isOutSideOverlapOnUpStation() {
        // Given
        Section target = new Section(이호선, 잠실역, 강남역, 6);

        // When & Then
        assertTrue(section.isOutSideOverlapOnUpStation(target));
    }

    /**
     * criteria
     * 강남역 <---------10---------> 삼성역
     *
     * target
     *                             삼성역 <----6----> 잠실역
     */
    @DisplayName("구간의 하행역과 대상의 상행역이 일치한다")
    @Test
    void isOutSideOverlapOnDownStation() {
        // Given
        Section target = new Section(이호선, 삼성역, 잠실역, 6);

        // When & Then
        assertTrue(section.isOutSideOverlapOnDownStation(target));
    }

    /**
     * criteria
     * 강남역 <---------10---------> 삼성역
     *
     * target
     * 강남역 <----6----> 잠실역
     */
    @DisplayName("구간의 상행역과 대상의 상행역이 일치한다")
    @Test
    void isInSideOverlapOnUpStation() {
        // Given
        Section target = new Section(이호선, 강남역, 잠실역, 6);

        // When & Then
        assertTrue(section.isInSideOverlapOnUpStation(target));
    }

     /**
     * criteria
     * 강남역 <---------10---------> 삼성역
     *
     * target
     *            잠실역 <----6----> 삼성역
     */
    @DisplayName("구간의 하행역과 대상의 하행역이 일치한다")
    @Test
    void isInSideOverlapOnDownStation() {
        // Given
        Section target = new Section(이호선, 잠실역, 삼성역, 6);

        // When & Then
        assertTrue(section.isInSideOverlapOnDownStation(target));
    }
}