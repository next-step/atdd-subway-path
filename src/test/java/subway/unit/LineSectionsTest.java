package subway.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayBadRequestException;
import subway.line.model.Line;
import subway.line.model.LineSections;
import subway.line.model.Section;
import subway.station.model.Station;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LineSectionsTest {


    /**
     * Given 구간이 없는 노선에
     * When 구간을 추가 하면
     * Then 구간이 추가 된다.
     */
    @DisplayName("구간 추가 기능")
    @Test
    void add() {
        // given
        Section section = 기본_구간();
        Line 이호선 = 기본_노선("이호선", section);

        // when
        LineSections 이호선_구간 = 이호선.getLineSections();
        이호선_구간.add(section, 이호선);

        // then
        List<Station> stations = 이호선_구간.getStations();
        assertThat(stations.size()).isEqualTo(2L);
    }

    /**
     * Given 구간이 있는 노선에
     * When 구간의 가장 앞에 구간을 추가 하면
     * Then 구간이 추가 된다.
     */
    @DisplayName("구간 추가 기능 : 노선 앞")
    @Test
    void addAppendSectionFront() {
        // given
        Line 이호선 = 이호선_기본구간_설정();

        // when
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Station 교대역 = new Station(3L, "교대역");

        Section 구간_추가 = Section.builder()
                .upStation(교대역)
                .downStation(강남역)
                .distance(10L)
                .line(이호선)
                .build();

        LineSections 이호선_구간 = 이호선.getLineSections();
        이호선_구간.add(구간_추가, 이호선);

        // then
        assertThat(이호선_구간.getFirstStation()).isEqualTo(교대역);
        assertThat(이호선_구간.getLastStation()).isEqualTo(역삼역);
    }

    /**
     * Given 구간이 있는 노선에
     * When 구간의 가장 뒤에 구간을 추가 하면
     * Then 구간이 추가 된다.
     */
    @DisplayName("구간 추가 기능 : 노선 뒤")
    @Test
    void addAppendSectionBehind() {
        // given
        Line 이호선 = 이호선_기본구간_설정();

        // when
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        Station 선릉역 = new Station(3L, "선릉역");

        Section 구간_추가 = Section.builder()
                .upStation(역삼역)
                .downStation(선릉역)
                .distance(10L)
                .line(이호선)
                .build();

        LineSections 이호선_구간 = 이호선.getLineSections();
        이호선_구간.add(구간_추가, 이호선);

        // then
        assertThat(이호선_구간.getFirstStation()).isEqualTo(강남역);
        assertThat(이호선_구간.getLastStation()).isEqualTo(선릉역);
    }

    /**
     * Given 구간이 있는 노선에
     * When 노선에 상행역과 하행역이 모두 존재하는 구간을 추가하면
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("노선 추가 기능 : 역 중복")
    @Test
    void addAppendSectionAlreadyExistStation() {
        // given
        Line 이호선 = 이호선_기본구간_설정();

        // when
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");

        Section 구간_추가 = Section.builder()
                .upStation(강남역)
                .downStation(역삼역)
                .distance(10L)
                .line(이호선)
                .build();

        LineSections 이호선_구간 = 이호선.getLineSections();

        // then
        assertThatThrownBy(() -> 이호선_구간.add(구간_추가, 이호선))
                .isInstanceOf(SubwayBadRequestException.class);
    }

    /**
     * Given 구간이 있는 노선에
     * When 노선에 상행역과 하행역이 모두 존재하지 않는 구간을
     * Then 구간이 추가되지 않는다.
     */
    @DisplayName("노선 추가 기능 : 역 없음")
    @Test
    void addAppendSectionNotExistStation() {
        // given
        Line 이호선 = 이호선_기본구간_설정();

        // when
        Station 홍대역 = new Station(11L, "홍대역");
        Station 신촌역 = new Station(22L, "신촌역");

        Section 구간_추가 = Section.builder()
                .upStation(홍대역)
                .downStation(신촌역)
                .distance(10L)
                .line(이호선)
                .build();

        LineSections 이호선_구간 = 이호선.getLineSections();

        // then
        assertThatThrownBy(() -> 이호선_구간.add(구간_추가, 이호선))
                .isInstanceOf(SubwayBadRequestException.class);
    }

    /**
     * Given 구간을 가진 노선이 있을 때
     * When 상행선과 하행선을 요청하면
     * Then 역 목록이 반환된다.
     */
    @DisplayName("역 목록 조회 기능")
    @Test
    void getStations() {
        // given
        Line 이호선 = 이호선_기본구간_설정();

        // when
        LineSections 이호선_구간 = 이호선.getLineSections();
        List<Station> stations = 이호선_구간.getStations();

        // then
        assertThat(stations.size()).isEqualTo(2L);
    }

    /**
     * Given 구간을 가진 노선이 있을 떄
     * When 마지막 역의 구간을 삭제하면
     * Then 노선의 구간이 삭제된다
     */
    @DisplayName("역으로 구간 제거 기능")
    @Test
    void removeSectionByStation() {
        // given
        Line 이호선 = 이호선_기본구간_설정();
        Station 선릉역 = new Station(3L, "선릉역");
        구간_추가(이호선, 선릉역);

        // when/then
        LineSections 이호선_구간 = 이호선.getLineSections();
        이호선_구간.removeSectionByStation(선릉역);
    }

    /**
     * Given 1개의 구간을 가진 노선이 있을 떄
     * When 구간을 삭제하면
     * Then 노선의 구간이 삭제 되지 않는다.
     */
    @DisplayName("역으로 구간 제거 기능 : 최소 구간")
    @Test
    void removeSectionByStationMinimumSectionCount() {
        // given
        Line 이호선 = 이호선_기본구간_설정();
        Station 역삼역 = new Station(2L, "역삼역");

        // when/then
        LineSections 이호선_구간 = 이호선.getLineSections();
        assertThatThrownBy(() -> 이호선_구간.removeSectionByStation(역삼역))
                .isInstanceOf(SubwayBadRequestException.class);
    }


    /**
     * Given 구간을 가진 노선이 있을 떄
     * When 노선의 중간 역인 구간을 삭제하면
     * Then 구간이 삭제된다.
     */
    @DisplayName("역으로 구간 제거 기능 : 구간 중간 삭제")
    @Test
    void removeSectionByStationLastStation() {
        // given
        Line 이호선 = 이호선_기본구간_설정();
        Station 선릉역 = new Station(3L, "선릉역");
        구간_추가(이호선, 선릉역);

        // when/then
        LineSections 이호선_구간 = 이호선.getLineSections();
        List<Station> stations = 이호선.getStations();
        이호선_구간.removeSectionByStation(stations.get(1));
    }

    private Line 이호선_기본구간_설정() {
        Section 기본_구간 = 기본_구간();
        Line line = 기본_노선("이호선", 기본_구간);
        line.addSection(기본_구간);
        return line;
    }

    private Line 구간_추가(Line line, Station station) {
        Station downStation = line.getLineSections().getLastStation();
        Section build = Section.builder()
                .upStation(downStation)
                .downStation(station)
                .distance(10L)
                .build();
        line.addSection(build);
        return line;
    }

    private Line 기본_노선(String name, Section section) {
        Line line = Line.builder()
                .name(name)
                .color("bg-green-600")
                .build();
        return line;
    }

    private Section 기본_구간() {
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(2L, "역삼역");
        final long distance = 10L;

        return Section.builder()
                .upStation(강남역)
                .downStation(역삼역)
                .distance(distance)
                .build();
    }
}