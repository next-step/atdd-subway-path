package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class LineTest {

    @Test
    void getStations() {
    }

    @Test
    void addSection() {
        Line line = new Line("2호선","green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        line.addSection(upStation, downStation, 10);
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        Line line = new Line("2호선","green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        line.addSection(upStation, downStation, 10);
        Station middleStation = new Station("삼성역");
        assertThatThrownBy(() -> line.addSection(upStation, downStation, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("추가하는 구간의 상행역이 잘못되었습니다.");

    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        Line line = new Line("2호선","green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        line.addSection(upStation, downStation, 10);
        assertThatThrownBy(() -> line.addSection(upStation, downStation, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("추가하는 구간의 상행역이 잘못되었습니다.");
    }

    @Test
    void removeSection() {
        // given
        Line line = new Line("2호선","green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        line.addSection(upStation, downStation, 10);
        Station newDownStation = new Station("삼성역");
        line.addSection(downStation, newDownStation, 10);
        assertThat(line.getSections().size()).isEqualTo(2);

        // when
        line.removeSection(newDownStation);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // given
        Line line = new Line("2호선","green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("교대역");
        line.addSection(upStation, downStation, 10);

        // when, then
        assertThatThrownBy(() -> line.removeSection(downStation)).isInstanceOf(RuntimeException.class);
    }
}
