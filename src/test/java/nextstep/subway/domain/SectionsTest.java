package nextstep.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

class SectionsTest {

    private static final Field sectionIdField = ReflectionUtils.findField(Section.class, "id");
    private static final Field stationIdField = ReflectionUtils.findField(Station.class, "id");
    private static long testSectionId = 1L;
    private static long testStationId = 1L;

    @Test
    @DisplayName("모든역을 가져오는지 확인")
    void getSectionsTest() {
        // given
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Station 청계산역 = new Station("청계산역");
        Station 정자역 = new Station("정자역");
        지하철역일련번호주입(강남역);
        지하철역일련번호주입(양재역);
        지하철역일련번호주입(청계산역);
        지하철역일련번호주입(정자역);


        Sections sections = new Sections();
        Section section1 = new Section(강남역, 양재역, 10);
        Section section2 = new Section(양재역, 청계산역, 10);
        Section section3 = new Section(청계산역, 정자역, 10);
        지하철구간일련번호주입(section1);
        지하철구간일련번호주입(section2);
        지하철구간일련번호주입(section3);
        sections.addSection(section1);
        sections.addSection(section2);
        sections.addSection(section3);

        // when
        List<Station> stations = sections.getStations();

        // then
        Assertions.assertThat(stations)
                .map(Station::getName)
                .containsExactly("강남역", "양재역", "청계산역", "정자역");
    }

    private void 지하철구간일련번호주입(Section section) {
        sectionIdField.setAccessible(true);
        ReflectionUtils.setField(sectionIdField, section, testSectionId++);
        sectionIdField.setAccessible(false);
    }

    private void 지하철역일련번호주입(Station station) {
        stationIdField.setAccessible(true);
        ReflectionUtils.setField(stationIdField, station, testStationId++);
        stationIdField.setAccessible(false);
    }
}