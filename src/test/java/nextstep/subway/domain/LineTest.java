package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.SubwayFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        Section 구간 = 구간_생성(분당선, "수원역", "매탄권선역", 5);

        //when
        분당선.addSection(구간);

        //then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @DisplayName("노선의 역을 조회한다.")
    @Test
    void getStations() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));
        분당선.addSection(구간_생성(분당선, "매탄권선역", "망포역", 5));

        //when
        List<Station> stations = 분당선.getStations();

        //then
        assertThat(stations).hasSize(3)
                .extracting(Station::getName)
                .containsExactlyInAnyOrder("수원역", "매탄권선역", "망포역");
    }

    @DisplayName("하행 종점역을 제거한다.")
    @Test
    void removeSectionInFinalDown() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));
        분당선.addSection(구간_생성(분당선, "매탄권선역", "망포역", 5));

        //when
        분당선.removeSection(역_생성("망포역"));

        //then
        assertThat(분당선.getStations()).hasSize(2);
        assertThat(extractStationNames(분당선.getStations())).containsExactlyInAnyOrder("수원역", "매탄권선역");
    }

    @DisplayName("하나 남은 구간의 하행 종점역 제거를 실패한다.")
    @Test
    void removeLeftOneSectionInFinalDown() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));

        //when
        assertThatThrownBy(() -> 분당선.removeSection(역_생성("매탄권선역"))).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행 종점역을 제거한다.")
    @Test
    void removeSectionInFinalUp() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));
        분당선.addSection(구간_생성(분당선, "매탄권선역", "망포역", 5));

        //when
        분당선.removeSection(역_생성("수원역"));

        //then
        assertThat(분당선.getStations()).hasSize(2);
        assertThat(extractStationNames(분당선.getStations())).containsExactlyInAnyOrder("매탄권선역", "망포역");
    }

    @DisplayName("하나 남은 구간의 상행 종점역 제거를 실패한다.")
    @Test
    void removeLeftOneSectionInFinalUp() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));

        //when
        assertThatThrownBy(() -> 분당선.removeSection(역_생성("수원역"))).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 중간 구간을 제거한다.")
    @Test
    void removeSectionInMiddle() {

        //given
        Line 분당선 = 노선_생성("분당선", "yellow");
        분당선.addSection(구간_생성(분당선, "수원역", "매탄권선역", 5));
        분당선.addSection(구간_생성(분당선, "매탄권선역", "망포역", 5));

        //when
        분당선.removeSection(역_생성("매탄권선역"));

        //then
        assertThat(분당선.getStations()).hasSize(2);
        assertThat(extractStationNames(분당선.getStations())).containsExactlyInAnyOrder("수원역", "망포역");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        Line 분당선 = 노선_생성("분당선", "yellow");

        //when
        분당선.update(노선_생성("신분당선", "red"));

        //then
        assertThat(분당선.getName()).isEqualTo("신분당선");
        assertThat(분당선.getColor()).isEqualTo("red");
    }

    private List<String> extractStationNames(List<Station> stations) {
        return stations.stream().map(Station::getName).collect(Collectors.toList());
    }
}
