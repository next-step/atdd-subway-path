package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionTest {
    private Station 강남역;
    private Station 정자역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        정자역 = new Station("정자역");
    }

    @Test
    @DisplayName("구간의 길이와 같거나 크면 true를 반환한다")
    void isGreaterOrEqualDistance() {
        final int LINE_DISTANCE = 10;
        Section section = new Section(null, 강남역, 정자역, LINE_DISTANCE);

        // when, then
        assertThat(section.isGreaterOrEqualDistance(
                new Section(null, 강남역, 정자역, LINE_DISTANCE))).isTrue();
        assertThat(section.isGreaterOrEqualDistance(
                new Section(null, 강남역, 정자역, LINE_DISTANCE - 1))).isTrue();
    }

    @Test
    @DisplayName("구간의 상행역과 하행역이 모두 동일하면 true를 반환한다")
    void equalsUpStationAndDownStation() {
        Section section = new Section(null, 강남역, 정자역, 10);

        // when, then
        assertThat(section.equalsUpStationAndDownStation(
                new Section(null, 강남역, 정자역, 10))).isTrue();
    }
}
