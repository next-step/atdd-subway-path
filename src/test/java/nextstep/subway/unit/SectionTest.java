package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionTest {

    @Test
    void testIsSameWithUpStation() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Section section = new Section(강남역, 역삼역, 10);

        assertThat(section.isSameWithUpStation(강남역)).isTrue();
        assertThat(section.isSameWithUpStation(역삼역)).isFalse();
    }
}
