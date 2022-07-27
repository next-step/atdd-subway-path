package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.unit.LineTestUtil.*;
class LineSectionTest {

    Line 일호선 = new Line(일호선ID, 일호선이름, 라인색  );

    @DisplayName("기존 구간의 역을 기준으로 그 사이에 구간 추가.")
    @Test
    void sectionBetWeenAdd() {
        일호선.addSection(1L, 개봉역, 구일역, DEFAULT_DISTANCE);
        일호선.addSection(2L, 개봉역, 구로역, 5);

        assertThat(일호선.getStations())
                .containsExactly(개봉역, 구로역, 구일역);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void firstSectionAdd() {
        일호선.addSection(1L, 개봉역, 구일역, DEFAULT_DISTANCE);
        일호선.addSection(2L, 구로역, 개봉역, 5);

        assertThat(일호선.getStations())
                .containsExactly(구로역, 개봉역, 구일역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void lastSectionAdd() {
        일호선.addSection(1L, 개봉역, 구일역, DEFAULT_DISTANCE);
        일호선.addSection(2L, 구일역, 구로역, 5);

        assertThat(일호선.getStations())
                .containsExactly(개봉역, 구일역, 구로역);
    }


}
