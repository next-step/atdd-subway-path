package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 양재역 = new Station("양재역");

        Line 신분당선 = new Line("신분당선", "yellow", 강남역, 판교역, 10);
        Section 판교_양재 = new Section(신분당선, 판교역, 양재역, 10);

        // when
        신분당선.addSection(판교_양재);

        // then
        assertThat(신분당선.getSectionSize()).isEqualTo(2);
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역, 양재역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 양재역 = new Station("양재역");

        Line 신분당선 = new Line("신분당선", "yellow", 강남역, 판교역, 10);
        Section 판교_양재 = new Section(신분당선, 판교역, 양재역, 10);
        신분당선.addSection(판교_양재);

        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 판교역, 양재역);
    }

    @DisplayName("구간 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 판교역 = new Station("판교역");
        Station 양재역 = new Station("양재역");

        Line 신분당선 = new Line("신분당선", "yellow", 강남역, 판교역, 10);
        Section 판교_양재 = new Section(신분당선, 판교역, 양재역, 10);
        신분당선.addSection(판교_양재);

        // when
        신분당선.removeSectionByLastDownStation(양재역);

        // then
        assertThat(신분당선.getSectionSize()).isEqualTo(1);
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역);
    }
}
