package nextstep.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class SectionsTest {

    @Test
    @DisplayName("모든역을 가져오는지 확인")
    void getSectionsTest() {
        // given
        Sections sections = new Sections();
        Section section1 = new Section(new Station("강남역"), new Station("양재역"), 10);
        Section section2 = new Section(new Station("양재역"), new Station("청계산역"), 10);
        Section section3 = new Section(new Station("청계산역"), new Station("정자역"), 10);
        sections.addLast(section1);
        sections.addLast(section2);
        sections.addLast(section3);

        // when
        List<Station> stations = sections.getStations();

        // then
        Assertions.assertThat(stations)
                .map(Station::getName)
                .containsExactly("강남역", "양재역", "청계산역", "정자역");
    }
}