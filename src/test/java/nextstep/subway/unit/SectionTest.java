package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
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

}
