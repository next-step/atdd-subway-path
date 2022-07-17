package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("모든 구간의 지하철 역들을 반환한다.")
    void getStations() {
        //given
        Section section = new Section(StationTest.GANGNAM_STATION, StationTest.YEOKSAM_STATION, 10);
        Section section2 = new Section(StationTest.YEOKSAM_STATION, StationTest.SEOLLEUNG_STATION, 10);

        Sections sections = new Sections();
        sections.add(section);
        sections.add(section2);

        // then
        assertThat(sections.getStations()).containsOnly(new Station(1L, "강남역"),
                                                            new Station(2L, "역삼역"),
                                                            new Station(3L, "선릉역"));
    }
}