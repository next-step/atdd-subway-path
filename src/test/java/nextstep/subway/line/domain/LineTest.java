package nextstep.subway.line.domain;

import io.restassured.internal.assertion.Assertion;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {
    @Test
    void getStations() {
    }

    @Test
    void addSection() {
        Line line = new Line("2호선","green");
        Station upStation = new Station("홍대입구역");
        Station downStation = new Station("신촌역");
        line.addSection(upStation, downStation, 5);
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {

    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        Line line = new Line("2호선","green");
        Station upStation = new Station("홍대입구역");
        Station downStation = new Station("신촌역");
        line.addSection(upStation, downStation, 5);
        assertThatThrownBy(() -> line.addSection(upStation, downStation, 3)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void removeSection() {
        // given
        Line line = new Line("2호선","green");
        Station upStation = new Station("홍대입구역");
        Station downStation = new Station("신촌역");
        line.addSection(upStation, downStation, 5);
        Station newDownStation = new Station("이대역");
        line.addSection(downStation, newDownStation, 3);
        assertThat(line.getSections().size()).isEqualTo(2);

        // when
        line.removeSection(newDownStation.getId());

        // then
        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // given
        Line line = new Line("2호선","green");
        Station upStation = new Station("홍대입구역");
        Station downStation = new Station("신촌역");
        line.addSection(upStation, downStation, 5);

        // when, then
        assertThatThrownBy(() -> line.removeSection(downStation.getId())).isInstanceOf(RuntimeException.class);
    }
}
