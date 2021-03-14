package nextstep.subway.line.domain;

import nextstep.subway.exception.InValidUpStationException;
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
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 이호선 = new Line("이호선", "green");

        // when
        이호선.addSection(강남역, 역삼역, 10);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(1);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 삼성역 = new Station("삼성역");
        Line 이호선 = new Line("이호선", "green");
        이호선.addSection(강남역, 역삼역, 10);

        // then
        assertThatThrownBy(() -> 이호선.addSection(삼성역, 강남역, 10))
                .isInstanceOf(InValidUpStationException.class);
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
