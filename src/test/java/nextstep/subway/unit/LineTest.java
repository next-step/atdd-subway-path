package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("노선 단위 테스트")
class LineTest {
    @DisplayName("역 사이에 새로운 구간 추가")
    @Test
    void addSectionBetweenStations() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 양재시민의숲역 = new Station(2L, "양재시민의숲역");
        Section sectionForLine = new Section(강남역, 양재시민의숲역, 10);

        Line 신분당선 = new Line("신분당선", "bg-red-600", sectionForLine);

        Station 양재역 = new Station(3L, "양재역");
        Section newSection = new Section(강남역, 양재역, 4);

        // when
        신분당선.registerSection(newSection);

        // then : 강남역-양재역 & 양재역-양재시민의숲역 구간이 있어야함 각각 길이는 4, 6
        List<Section> sections = 신분당선.getSections();
        assertThat(sections).hasSize(2);

        Section firstSection = sections.get(0);
        assertSection(firstSection, "강남역", "양재역", 4);

        Section secondSection = sections.get(1);
        assertSection(secondSection, "양재역", "양재시민의숲역", 6);
    }

    private void assertSection(Section target, String upStationName, String downStationName, int distance) {
        assertThat(target.getUpStationName()).isEqualTo(upStationName);
        assertThat(target.getDownStationName()).isEqualTo(downStationName);
        assertThat(target.getDistance()).isEqualTo(distance);
    }

    @DisplayName("상행역에 새로운 구간 추가")
    @Test
    void addSectionUpStation() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 양재역 = new Station(2L, "양재역");
        Section sectionForLine = new Section(강남역, 양재역, 10);

        Line 신분당선 = new Line("신분당선", "bg-red-600", sectionForLine);

        Station 신논현역 = new Station(3L, "신논현역");
        Section newSection = new Section(신논현역, 강남역, 5);

        // when
        신분당선.registerSection(newSection);

        // then
        String 상행_종점 = 신분당선.getFirstStationName();
        assertThat(상행_종점).isEqualTo("신논현역");

        List<Section> sections = 신분당선.getSections();
        assertThat(sections).hasSize(2);

        Section firstSection = sections.get(0);
        assertSection(firstSection, "강남역", "양재역", 10);

        Section secondSection = sections.get(1);
        assertSection(secondSection, "신논현역", "강남역", 5);
    }

    @DisplayName("하행역에 새로운 구간 추가")
    @Test
    void addSectionDownStation() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 양재역 = new Station(2L, "양재역");
        Section sectionForLine = new Section(강남역, 양재역, 10);

        Line 신분당선 = new Line("신분당선", "bg-red-600", sectionForLine);

        Station 양재시민의숲역 = new Station(3L, "양재시민의숲역");
        Section newSection = new Section(양재역, 양재시민의숲역, 5);

        // when
        신분당선.registerSection(newSection);

        // then
        String 하행_종점 = 신분당선.getLastStationName();
        assertThat(하행_종점).isEqualTo("양재시민의숲역");

        List<Section> sections = 신분당선.getSections();
        assertThat(sections).hasSize(2);

        Section firstSection = sections.get(0);
        assertSection(firstSection, "강남역", "양재역", 10);

        Section secondSection = sections.get(1);
        assertSection(secondSection, "양재역", "양재시민의숲역", 5);
    }

    @DisplayName("Section을 순서대로 반환한다.")
    @Test
    void getSections() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 양재역 = new Station(2L, "양재역");
        Section sectionForLine = new Section(강남역, 양재역, 10);

        Line 신분당선 = new Line("신분당선", "bg-red-600", sectionForLine);

        Station 신논현역 = new Station(3L, "신논현역");
        Section newSection = new Section(신논현역, 강남역, 5);
        신분당선.registerSection(newSection);

        // when
        List<Section> sections = 신분당선.getSections();
        sections.forEach(section -> System.out.println(section.getUpStationName()));

        // then
        assertThat(sections.get(0).getUpStation()).isEqualTo(신논현역);
        assertThat(sections.get(1).getUpStation()).isEqualTo(강남역);
    }

    @Test
    void getStations() {
        
    }

    @Test
    void removeSection() {
    }
}
