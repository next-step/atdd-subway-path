package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.unit.SetupList.*;
import static org.assertj.core.api.Assertions.*;

class LineTest {

    private Line 사호선;

    // given
    @BeforeEach
    void setup(){
        사호선 = new Line("사호선","blue");
        사호선.addSection(new Section(사호선, 평촌역, 범계역, 3));
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {

        // when
        사호선.addSection(new Section(사호선, 인덕원역, 평촌역, 2));

        // then
        assertThat(사호선.getSections().getSectionStations()).containsExactly(인덕원역, 평촌역, 범계역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {

        // then
        assertThat(사호선.getSections().getSectionStations()).containsExactly(평촌역, 범계역);
    }

    @DisplayName("구간의 목록에서 첫번째 역 삭제")
    @Test
    void removeFirstSection() {
        // given
        사호선.addSection(new Section(사호선, 인덕원역, 평촌역, 2));

        // when
        사호선.removeSection(인덕원역);

        // then
        assertThat(사호선.getSections().getSectionStations()).containsExactly(평촌역, 범계역);
    }

    @DisplayName("구간의 목록에서 마지막 역 삭제")
    @Test
    void removeLastSection() {
        // given
        사호선.addSection(new Section(사호선, 인덕원역, 평촌역, 2));

        // when
        사호선.removeSection(범계역);

        // then
        assertThat(사호선.getSections().getSectionStations()).containsExactly(인덕원역, 평촌역);
    }
}
