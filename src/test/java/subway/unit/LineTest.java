package subway.unit;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.line.model.Line;
import subway.line.model.Section;
import subway.station.model.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Line 단위 테스트")
class LineTest {

    /**
     * Given 노선이 있고
     * When 구간을 만들면
     * Then 노선에 구간이 추가 된다.
     */
    @DisplayName("노선에 구간을 추가 한다")
    @Test
    void addSection() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Line line = Line.builder()
                .name("2호선")
                .color("bg-green-600")
                .build();

        final long distance = 10;

        // when
        Section section = Section.builder()
                .line(line)
                .upStation(강남역)
                .downStation(역삼역)
                .distance(distance)
                .build();
        line.addSection(section);

        // then
        assertThat(line.getLineSections().getSectionsCount()).isEqualTo(1);
    }

    /**
     * Given 노선이 있고
     * When 노선의 마지막에 구간을 만들면
     * Then 노선에 구간이 추가 된다.
     */
    @DisplayName("노선의 마지막에 구간을 추가 한다.")
    @Test
    void addSectionEndOfLine() {
        // given
        Line line = 이호선_기본구간_생성();

        // when
        Station 역삼역 = new Station(2L, "역삼역");
        Station 선릉역 = new Station(3L, "선릉역");

        final long distance = 10;
        Section section = Section.builder()
                .line(line)
                .upStation(역삼역)
                .downStation(선릉역)
                .distance(distance)
                .build();
        line.addSection(section);

        // then
        long sectionsCount = line.getLineSections().getSectionsCount();
        assertThat(sectionsCount).isEqualTo(2);
    }

    /**
     * Given 노선이 있고
     * When 노선의 처음에 구간을 만들면
     * Then 노선에 구간이 추가 된다.
     */
    @DisplayName("노선의 처음에 구간을 추가 한다.")
    @Test
    void addSectionFrontOfLine() {
        // given
        Line line = 이호선_기본구간_생성();

        // when
        Station 선릉역 = new Station(3L, "선릉역");
        Station 역삼역 = new Station(2L, "역삼역");
        final long distance = 8;

        Section section = Section.builder()
                .line(line)
                .upStation(선릉역)
                .downStation(역삼역)
                .distance(distance)
                .build();
        line.addSection(section);

        // then
        long sectionsCount = line.getLineSections().getSectionsCount();
        assertThat(sectionsCount).isEqualTo(2);
    }

    /**
     * Given 노선이 있고
     * When 노선의 중간 구간을 만들면
     * Then 노선에 구간이 추가 된다.
     */
    @DisplayName("노선의 중간에 구간을 추가 한다.")
    @Test
    void addSectionInMiddleOfLine() {
        // given
        Line line = 이호선_기본구간_생성();

        // when
        Station 선릉역 = new Station(3L, "선릉역");
        Station 역삼역 = new Station(2L, "역삼역");

        final long distance = 5;
        Section section = Section.builder()
                .line(line)
                .upStation(선릉역)
                .downStation(역삼역)
                .distance(distance)
                .build();
        line.addSection(section);

        // then
        long sectionsCount = line.getLineSections().getSectionsCount();
        assertThat(sectionsCount).isEqualTo(2);
    }


    /**
     * Given 구간이 있는 노선이 있고
     * When 역을 조회하면
     * Then 역 목록을 얻을 수 있다
     */
    @DisplayName("노선에서 역을 조회한다.")
    @Test
    void getStations() {
        // given
        Line line = 이호선_기본구간_생성();

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations.size()).isEqualTo(2);
    }

    /**
     * Given 구간이 2개 이상인 노선이 있고
     * When 마지막 역을 삭제 하려고 하면
     * Then 구간을 삭제할 수 있다
     */
    @DisplayName("노선에서 마지막 역의 구간을 삭제한다.")
    @Test
    void deleteSectionByStation() {
        // given
        Line line = 이호선_기본구간_생성();
        Section section = 기본구간에_구간추가(line);

        // when
        line.deleteSectionByStation(section.getDownStation());

        // then
        long sectionsCount = line.getLineSections().getSectionsCount();
        assertThat(sectionsCount).isEqualTo(1);
    }

    private Line 이호선_기본구간_생성() {
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");

        Line line = Line.builder()
                .name("2호선")
                .color("bg-green-600")
                .build();

        final long distance = 10;

        Section section = Section.builder()
                .line(line)
                .upStation(강남역)
                .downStation(역삼역)
                .distance(distance)
                .build();
        line.addSection(section);
        return line;
    }

    private Section 기본구간에_구간추가(Line line) {
        Station upStation = new Station(2L, "역삼역");
        Station downStation = new Station(5L, "잠실역");


        final long distance = 10;
        Section section = Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
        line.addSection(section);
        return section;
    }


}
