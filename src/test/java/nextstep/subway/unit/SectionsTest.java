package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static nextstep.subway.unit.LineFixtures.구간_생성;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private static Sections sections = new Sections();

    @BeforeAll
    static void setUpSections() {
        /* [Up Station ID] -(distance)- [Down Station ID] */
        // [1] -(1000 m)- [2]
        Section 최초_종점_구간 = 구간_생성(1L, 1L, 2L, 1000);
        sections.add(최초_종점_구간);

        // [1] -(200 m)- [3] -(800 m)- [2]
        Section 최초_종점_구간_사이에_추가될_두번째_구간 = 구간_생성(2L, 3L, 2L, 800);
        sections.add(최초_종점_구간_사이에_추가될_두번째_구간);

        // [1] -(200 m)- [3] -(800 m)- [2] -(500 m)- [4]
        Section 하행_종점역_뒤에_추가될_세번째_구간 = 구간_생성(3L, 2L, 4L, 500);
        sections.add(하행_종점역_뒤에_추가될_세번째_구간);

        // [5] -(300 m)- [1] -(200 m)- [3] -(800 m)- [2] -(500 m)- [4]
        Section 상행_종점역_앞에_추가될_네번째_구간 = 구간_생성(4L, 5L, 1L, 300);
        sections.add(상행_종점역_앞에_추가될_네번째_구간);

        // [5] -(300 m)- [1] -(200 m)- [3] -(600 m)- [6] -(200 m)- [2] -(500 m)- [4]
        Section 중간구간_사이에_추가될_다섯번째_구간 = 구간_생성(5L, 3L, 6L, 600);
        sections.add(중간구간_사이에_추가될_다섯번째_구간);
    }

    @DisplayName("구간에서 상행종점역 찾기")
    @Test
    void findEndUpStation() {
        //when
        Station endUpStation = sections.findEndUpStation();

        //then
        assertThat(endUpStation.getId()).isEqualTo(5L);
    }

    @DisplayName("구간에서 하행종점역 찾기")
    @Test
    void findEndDownStation() {
        //when
        Station endUpStation = sections.findEndDownStation();

        //then
        assertThat(endUpStation.getId()).isEqualTo(4L);
    }

    @DisplayName("구간에 존재하는 모든 역 사이의 길이 순서대로 나열하기")
    @Test
    void getDistances() throws NoSuchFieldException, IllegalAccessException {
        //when
        List<Section> sectionList = 구간리스트가져오기(sections);
        //then
        // [5] -(300 m)- [1] -(200 m)- [3] -(600 m)- [6] -(200 m)- [2] -(500 m)- [4]
        assertIsEqualToUpStationAndDownStationAndDistance(sectionList.get(3),
                5L, 1L, 300);
        assertIsEqualToUpStationAndDownStationAndDistance(sectionList.get(0),
                1L, 3L, 200);
        assertIsEqualToUpStationAndDownStationAndDistance(sectionList.get(4),
                3L, 6L, 600);
        assertIsEqualToUpStationAndDownStationAndDistance(sectionList.get(1),
                6L, 2L, 200);
        assertIsEqualToUpStationAndDownStationAndDistance(sectionList.get(2),
                2L, 4L, 500);
    }

    @DisplayName("구간에 존재하는 모든 역 중복없이 순서대로 나열하기")
    @Test
    void flatStations() {
        //when
        List<Station> stations = sections.flatStations();

        //then
        assertThat(stations.stream()
                .map(Station::getId)).containsExactly(5L, 1L, 3L, 6L, 2L, 4L);
    }

    private void assertIsEqualToUpStationAndDownStationAndDistance(Section section, Long expectedUpStationId, Long expectedDownStationId, int expectedDistance) {
        assertThat(section.getUpStation().getId()).isEqualTo(expectedUpStationId);
        assertThat(section.getDownStation().getId()).isEqualTo(expectedDownStationId);
        assertThat(section.getDistance()).isEqualTo(expectedDistance);
    }

    private List<Section> 구간리스트가져오기(Sections sections) throws NoSuchFieldException, IllegalAccessException {
        Field field = sections.getClass().getDeclaredField("sections");
        field.setAccessible(true);
        return (List<Section>) field.get(sections);
    }
}
