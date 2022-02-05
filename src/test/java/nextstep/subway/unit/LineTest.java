package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionInLast() {
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 정자역 = new Station("정자역");
        Line line = new Line("신분당선", "bg-red-600");

        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 양재역, 정자역, 10));
        System.out.println(line.allStations());
        assertThat(line.allStations()).containsExactly(강남역, 양재역, 정자역);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSectionInFirst() {
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 정자역 = new Station("정자역");
        Line line = new Line("신분당선", "bg-red-600");

        line.addSection(new Section(line, 강남역, 양재역, 10));
        line.addSection(new Section(line, 정자역, 강남역, 10));
        System.out.println(line.allStations());
        assertThat(line.allStations()).containsExactly(정자역, 강남역, 양재역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
    }
}
