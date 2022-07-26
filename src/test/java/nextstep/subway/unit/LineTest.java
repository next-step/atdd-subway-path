package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LineTest {
    @Test
    void addSection() {
        Station 당곡역_상행 = new Station("당곡역");
        Station 보라매역_하행 = new Station("보라매역");

        Line 신림선 = new Line("신림선", "blue-bg-300");
        신림선.addSection(당곡역_상행, 보라매역_하행, 10);

        assertThat(신림선.getSections())
                .filteredOn(section -> section.equals(new Section(신림선, 당곡역_상행, 보라매역_하행, 10)));
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
