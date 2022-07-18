package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 신분당선 = new Line("신분당선", "yellow");
        Section 지하철구간 = new Section(신분당선, 강남역, 역삼역 , 10);

        //when
        신분당선.addSection(지하철구간);

        //then
        assertThat(신분당선.getSections()).containsExactly(지하철구간);
    }

    @DisplayName("지하철노선의 지하철역 목록 가져오기")
    @Test
    void getStations() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 신분당선 = new Line("신분당선", "yellow");
        Section 지하철구간 = new Section(신분당선, 강남역, 역삼역 , 10);

        //when
        신분당선.addSection(지하철구간);

        //then
        assertThat(신분당선.getStations()).containsExactly(강남역,역삼역);
    }

    @DisplayName("지하철 노선 제거")
    @Test
    void removeSection() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line 신분당선 = new Line("신분당선", "yellow");
        Section 지하철구간 = new Section(신분당선, 강남역, 역삼역 , 10);
        List<Section> 신분당선_지하철_구간 = 신분당선.getSections();
        신분당선_지하철_구간.add(지하철구간);

        //when
        신분당선_지하철_구간.remove(지하철구간);

        //then
        assertThat(신분당선_지하철_구간).hasSize(0);
    }
}
