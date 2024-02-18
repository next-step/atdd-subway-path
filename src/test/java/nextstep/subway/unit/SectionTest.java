package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionTest {

    @Test
    void testIsSameWithStation() {
        //given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Section section = new Section(강남역, 역삼역, 10);

        //then
        assertAll(
            () -> assertThat(section.isSameWithUpStation(강남역)).isTrue(),
            () -> assertThat(section.isSameWithUpStation(역삼역)).isFalse(),
            () -> assertThat(section.isSameWithDownStation(역삼역)).isTrue(),
            () -> assertThat(section.isSameWithDownStation(강남역)).isFalse()
        );
    }
}
