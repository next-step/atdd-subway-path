package nextstep.subway.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * # Section 추가 조건
 * ## 역 사이에 새로운 역 등록
 * - 기존 구간에 신규 구간 추가
 * - 기존 구간과 신규 구간이 같을 경우 예외
 * - A-C에 A-B 추가 시 A-B, B-C가 생김
 * - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정
 * ## 상행 종점에 새로운 역 등록
 * - A-B 에 X-A 등록
 * - X-A - A-B가 된다
 * - 상행 종점은 X가 됨
 * ## 하행 종점에 새로운 역 등록
 * - A-B에 B-C 등록
 * - A-B - B-C가 된다
 * - 하행 종점은 C가 됨
 */
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
        신분당선.addSectionVer2(newSection);

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
        신분당선.addSectionVer2(newSection);

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
        신분당선.addSectionVer2(newSection);

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

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
