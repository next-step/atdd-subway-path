package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    Station 판교역 = new Station("판교역");
    Station 정자역 = new Station("정자역");
    Station 미금역 = new Station("미금역");

    @Test
    void addSection() {
        // given
        Line line = new Line("신분당선", "red");
        Section 판교_정자_구간 = new Section(line, 판교역, 정자역, 10);
        line.getSections().add(판교_정자_구간);

        Section 정자_미금_구간 = new Section(line, 정자역, 미금역, 10);

        // when
        line.addSection(정자역, 미금역, 10);

        // then
        assertThat(line.getSections()).contains(정자_미금_구간);
    }

    @Test
    void getStations() {
        // given
        Line line = new Line("신분당선", "red");
        Section 판교_정자_구간 = new Section(line, 판교역, 정자역, 10);
        Section 정자_미금_구간 = new Section(line, 정자역, 미금역, 10);
        line.getSections().add(판교_정자_구간);
        line.getSections().add(정자_미금_구간);

        // when, then
        assertThat(line.getStations()).containsAll(List.of(판교역, 정자역, 미금역));
    }

    @Test
    void removeSection() {
        // given
        Line line = new Line("신분당선", "red");
        Section 판교_정자_구간 = new Section(line, 판교역, 정자역, 10);
        Section 정자_미금_구간 = new Section(line, 정자역, 미금역, 10);
        line.getSections().add(판교_정자_구간);
        line.getSections().add(정자_미금_구간);

        // when
        line.removeSection(미금역);

        // then
        assertThat(line.getSections()).doesNotContain(정자_미금_구간);
    }
}
