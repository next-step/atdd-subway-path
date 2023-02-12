package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Sections;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {
    Sections sections;
    Station 신논역;
    Station 신사역;
    Station 강남역;
    Station 판교역;
    Station 논현역;
    Station 삼성역;
    Station 구로역;
    Station 개탄역;

    @BeforeEach
    void setup() {
        신논역 = new Station(1L, "신논역");
        신사역 = new Station(2L, "신사역");
        강남역 = new Station(3L, "강남역");
        판교역 = new Station(4L, "판교역");
        논현역 = new Station(5L, "논현역");
        삼성역 = new Station(6L, "삼성역");
        구로역 = new Station(7L, "구로역");
        개탄역 = new Station(8L, "구로역");

        Line line = new Line(1L, "line", "color");

        Section section1 = new Section(1L, line, 신논역, 신사역, 10);
        Section section2 = new Section(2L, line, 신사역, 강남역, 10);
        Section section3 = new Section(3L, line, 강남역, 판교역, 10);
        Section section4 = new Section(4L, line, 판교역, 논현역, 10);
        Section section5 = new Section(5L, line, 논현역, 삼성역, 10);

        sections = new Sections();
        sections.addSection(section1);
        sections.addSection(section2);
        sections.addSection(section3);
        sections.addSection(section4);
        sections.addSection(section5);
    }

    @Test
    @DisplayName("Section의 Station 출력 (순서 : 상행->하행)")
    void test1() {
        List<Station> stations = sections.getStations();
        assertThat(stations)
                .extracting(Station::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
    }

    @Test
    @DisplayName("Section 삭제 테스트 (마지막 섹션 삭제)")
    void test() {
        sections.removeSection(삼성역);
        assertThat(sections.getStations()).containsExactly(신논역, 신사역, 강남역, 판교역, 논현역);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B) + (B, C)")
    void test2() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 삼성역, 구로역, 5);
        sections.addSection(newSection);

        List<Station> stations = sections.getStations();
        assertThat(stations.get(stations.size() - 1)).isEqualTo(구로역);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B) + (A, C)")
    void test3() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 신논역, 구로역, 1);
        sections.addSection(newSection);

        List<Station> stations = sections.getStations();
        assertThat(stations.get(1)).isEqualTo(구로역);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B) + (C, B)")
    void test4() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 구로역, 신사역, 1);
        sections.addSection(newSection);

        List<Station> stations = sections.getStations();
        assertThat(stations.get(1)).isEqualTo(구로역);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B), (C, A)")
    void test5() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 구로역, 신논역, 1);
        sections.addSection(newSection);

        List<Station> stations = sections.getStations();
        assertThat(stations.get(1)).isEqualTo(신논역);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B : 10m) + (A, C : 20m)")
    void test6() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 신논역, 구로역, 20);
        assertThatThrownBy(() -> {
            sections.addSection(newSection);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B : 10m) + (C, B : 20m)")
    void test7() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 구로역, 신사역, 20);
        assertThatThrownBy(() -> {
            sections.addSection(newSection);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B) + (B, A)")
    void test8() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 신논역, 신사역, 20);
        assertThatThrownBy(() -> {
            sections.addSection(newSection);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Section 추가 테스트 : (A, B) + (C, D)")
    void test9() {
        Line line = new Line(1L, "name", "color");
        Section newSection = new Section(6L, line, 구로역, 개탄역, 20);
        assertThatThrownBy(() -> {
            sections.addSection(newSection);
        }).isInstanceOf(IllegalArgumentException.class);
    }


}
