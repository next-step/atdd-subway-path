package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static nextstep.subway.unit.LineFixtures.구간_생성;
import static nextstep.subway.unit.LineFixtures.역_생성;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private Sections sections = new Sections();
    /* [Up Station ID] -(distance)- [Down Station ID] */
    // [1] -(1000 m)- [2]
    Section 구간1_1역_2역 = 구간_생성(1L, 1L, 2L, 1000);
    // [1] -(200 m)- [3] -(800 m)- [2]
    Section 구간2_3역_2역 = 구간_생성(2L, 3L, 2L, 800);
    // [1] -(200 m)- [3] -(800 m)- [2] -(500 m)- [4]
    Section 구간3_2역_4역 = 구간_생성(3L, 2L, 4L, 500);
    // [5] -(300 m)- [1] -(200 m)- [3] -(800 m)- [2] -(500 m)- [4]
    Section 구간4_5역_1역 = 구간_생성(4L, 5L, 1L, 300);
    // [5] -(300 m)- [1] -(200 m)- [3] -(600 m)- [6] -(200 m)- [2] -(500 m)- [4]
    Section 구간5_3역_6역 = 구간_생성(5L, 3L, 6L, 600);

    @BeforeEach
    void setUpSections() {
        sections.add(구간1_1역_2역);
        sections.add(구간2_3역_2역);
        sections.add(구간3_2역_4역);
        sections.add(구간4_5역_1역);
        sections.add(구간5_3역_6역);
    }

    @AfterEach
    void clean() {
        sections = new Sections();
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

    @DisplayName("구간 사이에 새로운 구간 추가하고, 구간 역사이 길이 재할당")
    @Test
    void addSectionToMiddle() {
        //given
        // [5] -(300 m)- [1] -(200 m)- [3] -(600 m)- [6] -(200 m)- [2] -(500 m)- [4]
        Station 새로_추가될_역 = 역_생성(7L);
        // 3 - 6 사이에 7을 추가
        Section 새로_추가될_구간 = 구간_생성(6L, 3L, 새로_추가될_역.getId(), 300);

        //when then
        // [5] -(300 m)- [1] -(200 m)- [3] -(300m)- [7] -(300 m)- [6] -(200 m)- [2] -(500 m)- [4]
        assertThat(sections.flatStations().stream()
                .map(Station::getId)).containsExactly(5L, 1L, 3L, 6L, 2L, 4L);
        assertThat(구간5_3역_6역.getDistance()).isEqualTo(600);
        sections.add(새로_추가될_구간);
        assertThat(구간5_3역_6역.getDistance()).isEqualTo(300);
        assertThat(sections.flatStations().stream()
                .map(Station::getId)).containsExactly(5L, 1L, 3L, 7L, 6L, 2L, 4L);
    }

    @DisplayName("구간 사이에 구간을 삭제하고, 구간 역사이 길이 재할당")
    @Test
    void deleteSectionFromMiddle() {
        //given
        // [5] -(300 m)- [1] -(200 m)- [3] -(600 m)- [6] -(200 m)- [2] -(500 m)- [4]
        Station 삭제할_구간의_중간_역 = 역_생성(6L);

        //when then
        // [5] -(300 m)- [1] -(200 m)- [3] -(800 m)- [2] -(500 m)- [4]
        assertThat(구간5_3역_6역.getDistance()).isEqualTo(600);
        assertThat(sections.flatStations().stream()
                .map(Station::getId)).containsExactly(5L, 1L, 3L, 6L, 2L, 4L);
        sections.delete(삭제할_구간의_중간_역);
        assertThat(구간5_3역_6역.getDistance()).isEqualTo(800);
        assertThat(sections.flatStations().stream()
                .map(Station::getId)).containsExactly(5L, 1L, 3L, 2L, 4L);
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
