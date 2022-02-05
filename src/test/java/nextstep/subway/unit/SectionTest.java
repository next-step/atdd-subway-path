package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    private static int 거리;
    private static Station 하행역;
    private static Station 상행역;

    @Test
    void Section_생성_테스트() {
        Section 구간 = 상행역_하행역_의_구간을_만든다();

        assertThat(구간.getUpStation()).isEqualTo(상행역);
        assertThat(구간.getDownStation()).isEqualTo(하행역);
    }

    @DisplayName("구간에 노선을 설정하여 관계 맺기를 성공한다.")
    @Test
    void Section_setLine_테스트() {
        // given
        Line 노선 = Line.of( "일호선", "색상");
        Section 구간 = 상행역_하행역_의_구간을_만든다();

        // when
        구간.setLine(노선);

        // then
        assertThat(노선.getSections().size()).isEqualTo(1);
        assertThat(구간.getLine().getName()).isEqualTo(노선.getName());
    }

    public static Section 상행역_하행역_의_구간을_만든다() {
        상행역 = Station.of("상행역");
        하행역 = Station.of("하행역");
        거리 = 10;

        return Section.of(상행역, 하행역, 거리);
    }
}
