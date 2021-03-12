package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.station.domain.Station;

public class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        신분당선 = new Line("신분당선", "red", 강남역, 역삼역, 5);
    }

    @Test
    void getStations() {
    }

    @Test
    void addSection() {
        // given
        Station 판교역 = new Station("판교역");
        int expectedSize = 신분당선.getSections().size() + 1;

        // when
        신분당선.addSection(new Section(신분당선, 역삼역, 판교역, 3));

        // then
        assertThat(신분당선.getSections().size()).isEqualTo(expectedSize);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
    }

    @Test
    void removeSection() {
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
    }
}
