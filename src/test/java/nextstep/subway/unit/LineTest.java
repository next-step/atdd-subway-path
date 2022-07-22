package nextstep.subway.unit;

import static nextstep.subway.utils.EntityCreator.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;

class LineTest {

    @Test
    void 구간_추가() {
        // given
        Line 신분당선 = createLine("신분당선", "bg-red-600");
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);

        // when
        신분당선.addSection(판교_정자);

        // then
        assertThat(신분당선.getSections()).contains(판교_정자);
    }

    @Test
    void 모든_역_가져오기() {
        // given
        Line 신분당선 = createLine("신분당선", "bg-red-600");
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);
        신분당선.addSection(판교_정자);

        // when
        List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 판교역));
    }

    @Test
    void 구간_삭제_성공() {
        // given
        Line 신분당선 = createLine("신분당선", "bg-red-600");
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Station 미금역 = createStation("미금역");
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);
        Section 정자_미금 = createSection(신분당선, 정자역, 미금역);
        신분당선.addSection(판교_정자);
        신분당선.addSection(정자_미금);

        // when
        신분당선.deleteSection(미금역);

        // then
        assertThat(신분당선.getSections()).hasSize(1).doesNotContain(정자_미금);
        assertThat(신분당선.getStations()).hasSize(2).doesNotContain(미금역);
    }

    @Test
    void 구간_삭제_실패() {
        // given
        Line 신분당선 = createLine("신분당선", "bg-red-600");
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Station 미금역 = createStation("미금역");
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);
        Section 정자_미금 = createSection(신분당선, 정자역, 미금역);
        신분당선.addSection(판교_정자);
        신분당선.addSection(정자_미금);

        // when
        assertThatThrownBy(() -> 신분당선.deleteSection(정자역))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        assertThat(신분당선.getSections()).hasSize(2)
                        .containsExactlyInAnyOrderElementsOf(List.of(판교_정자, 정자_미금));
        assertThat(신분당선.getStations()).hasSize(3)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 판교역, 미금역));
    }
}
