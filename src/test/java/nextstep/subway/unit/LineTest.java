package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    Line 일호선_생성() {
        Line line = new Line("1호선", "blue");

        Station 구일역 = new Station("구일역");
        Station 영등포역 = new Station("영등포역");
        final int 구일역_영등포역_거리 = 13;

        line.addSection(new Section(line, 구일역, 영등포역, 구일역_영등포역_거리));

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




    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        Line 일호선 = 일호선_생성();
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

    @DisplayName("지하철 노선에 구간 추가 실패(매칭되는 기존 구간보다 거리가 더 긺)")
    @Test
    void addSectionFail() {
        // given
        Line 일호선 = 일호선_생성();
        Section 구일역_구로역_구간 = 구일역_구로역_구간생성(일호선, 10);

        // then
        assertThatThrownBy(() -> 일호선.addSection(구일역_구로역_구간)).hasMessageContaining("긴 구간");
    }


    @DisplayName("지하철 노선에 등록된 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = 일호선_생성();

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .contains("구로역", "신도림역");
    }

    @DisplayName("지하철 노선에서 구간 제거")
    @Test
    void removeSection() {
        // given
        Line line = 일호선_생성();

        Station 신도림역 = new Station("신도림역");
        Station 영등포역 = new Station("영등포역");

        line.addSection(new Section(line, 신도림역, 영등포역, 15));

        // when
        line.removeSection();

        // then
        List<Station> stations = line.getStations();
        assertThat(stations.stream().map(Station::getName)
                .collect(Collectors.toList()))
                .contains("구로역", "신도림역");

    }

    @DisplayName("지하철 노선에서 구간 제거 실패")
    @Test
    void removeSectionFail() {
        // given
        Line line = 일호선_생성();   // 1호선에 구로역-신도림역 구간 1개 존재

        // then
        assertThatThrownBy(line::removeSection).isInstanceOf(IllegalArgumentException.class);
    }
}
