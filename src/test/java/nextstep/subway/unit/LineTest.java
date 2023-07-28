package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given
        Station 판교역 = new Station(1L, "판교역");
        Station 정자역 = new Station(2L, "정자역");
        Station 미금역 = new Station(3L, "미금역");

        Line line = new Line(1L, "신분당선", "red", new ArrayList<>());
        Section 판교_정자_구간 = new Section(1L, line, 판교역, 정자역, 10);
        line.getSections().add(판교_정자_구간);

        Section 정자_미금_구간 = new Section(line, 정자역, 미금역, 10);

        // when
        line.addSection(정자역, 미금역, 10);

        // then
        assertThat(line.getSections()).contains(정자_미금_구간);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
