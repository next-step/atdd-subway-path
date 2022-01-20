package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTest {
    private Station 강남역;
    private Station 역삼역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
    }

    @Test
    void getStations() {
    }

    @DisplayName("노선에 구간을 추가하면, 노선의 크기가 증가한다.")
    @Test
    void addSection() {
        // given
        Station 삼성역 = new Station("삼성역");
        int expected = 이호선.size() + 1;

        // when
        이호선.addSection(new Section(이호선, 역삼역, 삼성역, 10));

        // then
        assertThat(이호선.size()).isEqualTo(expected);
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
