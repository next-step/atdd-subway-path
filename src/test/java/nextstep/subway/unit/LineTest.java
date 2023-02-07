package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LineTest {
    @Test
    void addSection() {
        // given
        Line 신분당선 = new Line("신분당선", "bg-red-900");
        int distance = 10;

        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Section section = new Section(신분당선, 강남역, 양재역, distance);

        // when
        신분당선.addSection(section);

        // then
        assertThat(신분당선.getSections()).containsExactlyElementsOf(List.of(section));
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
