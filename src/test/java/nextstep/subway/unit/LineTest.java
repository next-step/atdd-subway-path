package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    Line 일호선_생성(Station upStation, Station downStaion, int distance) {
        Line line = new Line("1호선", "blue");
        line.addSection(new Section(line, upStation, downStaion, distance));

        return line;
    }

    Section 구일역_구로역_구간생성(Line line, int distance) {
        Station 구일역 = new Station("구일역");
        Station 구로역 = new Station("구로역");
        int 구일역_구로역_거리 = distance;

        return new Section(line, 구일역, 구로역, 구일역_구로역_거리);
    }

    Section 구로역_신도림역_구간생성(Line line, int distance) {
        Station 구로역 = new Station("구로역");
        Station 신도림역 = new Station("신도림역");
        int 구로역_신도림역_거리 = distance;

        return new Section(line, 구로역, 신도림역, 구로역_신도림역_거리);
    }

    Section 신도림역_영등포역_구간생성(Line line, int distance) {
        Station 신도림역 = new Station("신도림역");
        Station 영등포역 = new Station("영등포역");
        int 신도림역_영등포역_거리 = distance;

        return new Section(line, 신도림역, 영등포역, 신도림역_영등포역_거리);
    }


    @DisplayName("지하철 노선에 구간 추가(A역-C역 사이에 B역을 추가)")
    @Test
    void addSection() {
        // given
        Line 일호선 = 일호선_생성(new Station("구일역"), new Station("영등포역"), 13);
        Section 구일역_구로역_구간 = 구일역_구로역_구간생성(일호선, 6);
        Section 구로역_신도림역_구간 = 구로역_신도림역_구간생성(일호선, 3);

        // when
        일호선.addSection(구일역_구로역_구간);
        일호선.addSection(구로역_신도림역_구간);

        // then
        List<Station> stations = 일호선.getStations();
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .containsExactly("구일역", "구로역", "신도림역", "영등포역");
    }

    @DisplayName("지하철 노선에 구간 추가(새로운 역을 상행 종점으로 등록)")
    @Test
    void addSectionWithNewLastUpStation() {
        // given
        Line 일호선 = 일호선_생성(new Station("구로역"), new Station("신도림역"), 10);
        Section 구일역_구로역_구간 = 구일역_구로역_구간생성(일호선, 6);

        // when
        일호선.addSection(구일역_구로역_구간);

        // then
        List<Station> stations = 일호선.getStations();
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .containsExactly("구일역", "구로역", "신도림역");
    }

    @DisplayName("지하철 노선에 구간 추가(새로운 역을 하행 종점으로 등록)")
    @Test
    void addSectionWithNewLastDownStation() {
        // given
        Line 일호선 = 일호선_생성(new Station("구일역"), new Station("구로역"), 10);
        Section 구로역_신도림역_구간 = 구로역_신도림역_구간생성(일호선, 6);
        Section 신도림역_영등포역_구간 = 신도림역_영등포역_구간생성(일호선, 2);

        // when
        일호선.addSection(구로역_신도림역_구간);
        일호선.addSection(신도림역_영등포역_구간);

        // then
        List<Station> stations = 일호선.getStations();
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .containsExactly("구일역", "구로역", "신도림역", "영등포역");
    }

    @DisplayName("지하철 노선에 구간 추가 실패(매칭되는 기존 구간보다 거리가 더 긺)")
    @Test
    void addSectionFailLongerDistance() {
        // given
        Line 일호선 = 일호선_생성(new Station("구일역"), new Station("신도림역"), 7);
        Section 구일역_구로역_구간 = 구일역_구로역_구간생성(일호선, 10);

        // then
        assertThatThrownBy(() -> 일호선.addSection(구일역_구로역_구간)).hasMessageContaining("긴 구간");
    }

    @DisplayName("지하철 노선에 구간 추가 실패(이미 등록되어 있는 구간)")
    @Test
    void addSectionFailAlreadyRegistered() {
        // given
        Line 일호선 = 일호선_생성(new Station("구일역"), new Station("신도림역"), 7);
        Section 구일역_구로역_구간 = 구일역_구로역_구간생성(일호선, 5);
        일호선.addSection(구일역_구로역_구간);

        // then
        assertThatThrownBy(() -> 일호선.addSection(구일역_구로역_구간)).hasMessageContaining("이미 노선에 등록된");
    }

    @DisplayName("지하철 노선에 구간 추가 실패(상행역 하행역 둘 중 하나도 포함되어 있지 않음)")
    @Test
    void addSectionFailNoneMatchingUpAndDownStations() {
        // given
        Line 일호선 = 일호선_생성(new Station("신도림역"), new Station("영등포역"), 7);
        Section 구일역_구로역_구간 = 구일역_구로역_구간생성(일호선, 5);

        // then
        assertThatThrownBy(() -> 일호선.addSection(구일역_구로역_구간)).hasMessageContaining("기존 구간에 존재하지 않습니다");
    }

    @DisplayName("지하철 노선에서 중간역 제거")
    @Test
    void removeMiddleStation() {
        // given
        Station 구일역 = new Station("구일역");
        Station 구로역 = new Station("구로역");

        Line 일호선 = 일호선_생성(구일역, 구로역, 10);
        Section 구로역_신도림역_구간 = 구로역_신도림역_구간생성(일호선, 7);
        일호선.addSection(구로역_신도림역_구간);

        // when
        일호선.removeSection(구로역);

        // then
        List<Station> stations = 일호선.getStations();
        Sections sections = 일호선.getSections();
        Section section = sections.findSection("구일역", "신도림역");

        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .contains("구일역", "신도림역");
        assertThat(section.getDistance()).isEqualTo(17);
    }

    @DisplayName("지하철 노선에서 하행종점역 제거")
    @Test
    void removeLastDownStation() {
        // given
        Station 구일역 = new Station("구일역");
        Station 구로역 = new Station("구로역");

        Line 일호선 = 일호선_생성(구일역, 구로역, 10);
        Section 구로역_신도림역_구간 = 구로역_신도림역_구간생성(일호선, 7);
        일호선.addSection(구로역_신도림역_구간);

        // when
        Station 신도림역 = 구로역_신도림역_구간.getDownStation();
        일호선.removeSection(신도림역);

        // then
        List<Station> stations = 일호선.getStations();
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .contains("구일역", "구로역");
    }


    @DisplayName("지하철 노선에서 상행종점역 제거")
    @Test
    void removeLastUpStation() {
        // given
        Station 구로역 = new Station("구로역");
        Station 신도림역 = new Station("신도림역");

        Line 일호선 = 일호선_생성(구로역, 신도림역, 7);
        Section 구일역_구로역_구간 = 구일역_구로역_구간생성(일호선, 10);
        일호선.addSection(구일역_구로역_구간);

        // when
        Station 구일역 = 구일역_구로역_구간.getUpStation();
        일호선.removeSection(구일역);

        // then
        List<Station> stations = 일호선.getStations();
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .contains("구로역", "신도림역");
    }

    @DisplayName("지하철 노선에서 구간 제거 실패")
    @Test
    void removeSectionFail() {
        // given
        Line line = 일호선_생성(new Station("구일역"), new Station("구로역"), 10);   // 1호선에 구간이 1개만 존재하는 상태
        Station 강남역 = new Station("강남역");

        // then
        assertThatThrownBy(() -> line.removeSection(강남역)).hasMessageContaining("존재하지 않는 역");
    }
}
