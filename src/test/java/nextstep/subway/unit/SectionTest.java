package nextstep.subway.unit;

import static nextstep.subway.common.LineSomething.DISTANCE_BASIC;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    void Section_생성_테스트() {
        final Station 상행역 = Station.of("상행역");
        final Station 하행역 = Station.of("하행역");
        final int 거리 = 10;
        Section section = Section.of(상행역, 하행역, 거리);

        assertThat(section.getUpStation()).isEqualTo(상행역);
        assertThat(section.getDownStation()).isEqualTo(하행역);
    }

    @DisplayName("구간에 노선을 설정하여 관계 맺기를 성공한다.")
    @Test
    void Section_setLine_테스트() {
        Line 노선 = Line.of( "일호선", "색상");

        Station 역1 = Station.of(1L, "역1");
        Station 역2 = Station.of(2L, "역2");

        Section 구간 = Section.of(역1, 역2, DISTANCE_BASIC);
        구간.setLine(노선);

        assertThat(노선.getSections().size()).isEqualTo(1);
        assertThat(구간.getLine().getName()).isEqualTo(노선.getName());
    }

}
