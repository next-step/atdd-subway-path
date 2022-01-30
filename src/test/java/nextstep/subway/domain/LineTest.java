package nextstep.subway.domain;

import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.StationStepUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class LineTest extends AcceptanceTest {

    /**
     *
     */

    @DisplayName("노선 중간에 구간을 추가할 수 있다.")
    @Test
    void isNotDownStation() {
        Line line = new Line();
        Station 상행역 = new Station(1L,기존지하철);
        Station 하행역 = new Station(2L,새로운지하철);
        Station 중간역 = new Station(3L,"중간역");
        line.initSection(Section.of(상행역,하행역, 역간_거리));

        line.addSection(Section.of(상행역,중간역,역간_거리-1));
        assertThat(line.getSectionSize()).isEqualTo(2);
    }
}
