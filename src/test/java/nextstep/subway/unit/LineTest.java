package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LineTest {
    private Station 역삼역;
    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void init() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        삼성역 = new Station(4L, "삼성역");
        이호선 = new Line(1L, "노선", "red", 역삼역, 삼성역, 10);
    }

    @DisplayName("노선에 맨앞에 구간을 추가한다.")
    @Test
    void addSection_first() {
        이호선.addSection(강남역, 역삼역, 10);

        assertThat(이호선.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 삼성역));
        assertThat(이호선.totalDistance()).isEqualTo(20);
    }

    @DisplayName("노선에 맨앞에 구간을 추가할 때, 추가구간의 상행선이 기존에 포함되면 오류가 발생한다.")
    @Test
    void addSection_first_duplicate() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 이호선.addSection(삼성역, 역삼역, 10));
    }

    @DisplayName("노선의 중간에 구간을 추가한다.")
    @Test
    void addSection_middle() {
        이호선.addSection(역삼역, 선릉역, 5);

        assertThat(이호선.getStations()).containsExactlyElementsOf(Arrays.asList(역삼역, 선릉역, 삼성역));
        assertThat(이호선.totalDistance()).isEqualTo(10);
    }

    @DisplayName("노선의 중간에 구간을 추가할 때 추가구간의 하행선이 기존에 포함되면 오류가 발생한다.")
    @Test
    void addSection_middle_duplicate() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 이호선.addSection(역삼역, 삼성역, 10));
    }

    @DisplayName("노선의 중간에 구간을 추가할 때 기존구간의 길이보다 추가되는 길이가 같거나 크면 오류가 발생한다.")
    @Test
    void addSection_middle_invalid_distance() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 이호선.addSection(역삼역, 선릉역, 10));
    }

    @DisplayName("노선의 끝에 구간을 추가한다.")
    @Test
    void addSection_last() {
        이호선.addSection(삼성역, 선릉역, 5);

        assertThat(이호선.getStations()).containsExactlyElementsOf(Arrays.asList(역삼역, 삼성역, 선릉역));
        assertThat(이호선.totalDistance()).isEqualTo(15);
    }
    @DisplayName("노선에 끝에 구간을 추가할 때, 추가구간의 하행선이 기존에 포함되면 오류가 발생한다.")
    @Test
    void addSection_last_duplicate() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 이호선.addSection(삼성역, 역삼역, 10));
    }

    @DisplayName("노선에 구간을 추가할 때, 추가하는 구간의 길이가 1미만 일 수 없다.")
    @Test
    void addSection_distance_min() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 이호선.addSection(역삼역, 선릉역, 0));
    }

    @DisplayName("노선에 포함된 지하철역을 조회한다.")
    @Test
    void getStations() {
        final List<Station> stations = 이호선.getStations();

        assertThat(stations).containsExactlyElementsOf(Arrays.asList(역삼역, 삼성역));
    }

    @DisplayName("노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        이호선.addSection(삼성역, 선릉역, 10);

        이호선.removeSection(선릉역.getId());

        assertThat(이호선.getStations()).containsExactlyElementsOf(Arrays.asList(역삼역, 삼성역));
    }

    @DisplayName("노선의 구간이 하나일 때, 노선을 제거하면 오류가 발생한다.")
    @Test
    void removeSection_line_invalid_size() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 이호선.removeSection(삼성역.getId()));
    }

}
