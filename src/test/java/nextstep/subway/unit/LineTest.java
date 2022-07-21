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
    void addSection() {
        // given
        Line 신분당선 = createLine();
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);

        // when
        신분당선.addSection(판교_정자);

        // then
        assertThat(신분당선.getSections().contains(판교_정자)).isTrue();
    }

    @Test
    void getStations() {
        // given
        Line 신분당선 = createLine();
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);
        신분당선.addSection(판교_정자);

        // when
        List<Station> sections = 신분당선.getStations();

        // then
        assertThat(sections).hasSize(2)
                .containsExactlyInAnyOrderElementsOf(List.of(정자역, 판교역));
    }

    @Test
    void removeSection() {
        // given
        Line 신분당선 = createLine();
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
}
