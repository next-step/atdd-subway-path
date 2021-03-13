package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    }

    @Test
    void removeSection() {
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
    }
}
