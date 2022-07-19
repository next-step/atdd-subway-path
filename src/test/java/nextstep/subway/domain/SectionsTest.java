package nextstep.subway.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    void getOrderedSections() {
        final Line line = new Line(1L, "신분당선", "bg-red");
        final Section 강남_양재_구간 = new Section(line, new Station(1L, "강남역"), new Station(2L, "양재역"), 3);
        final Section 논현_강남_구간 = new Section(line, new Station(3L, "논현역"), new Station(1L, "강남역"), 3);

        line.addSection(강남_양재_구간);
        line.addSection(논현_강남_구간);

        assertThat(line.getSections())
                .containsSequence(논현_강남_구간, 강남_양재_구간);
    }

}