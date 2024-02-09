package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LineTest {
    private Station 역삼역;
    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;
    private Station 봉은사역;
    private Line 이호선;

    @BeforeEach
    void init() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        삼성역 = new Station(4L, "삼성역");
        봉은사역 = new Station(5L, "봉은사역");
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
        assertThatThrownBy(() -> { 이호선.addSection(삼성역, 역삼역, 10); })
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("이미 등록되어 있는 지하철역 입니다.");
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
        assertThatThrownBy(() -> { 이호선.addSection(역삼역, 삼성역, 10); })
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("이미 등록되어 있는 지하철역 입니다.");
    }

    @DisplayName("노선의 중간에 구간을 추가할 때 기존구간의 길이보다 추가되는 길이가 같거나 크면 오류가 발생한다.")
    @Test
    void addSection_middle_invalid_distance() {
        assertThatThrownBy(() -> { 이호선.addSection(역삼역, 선릉역, 10); })
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("중간에 추가되는 길이가 상행역의 길이보다 크거나 같을 수 없습니다.");
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
        assertThatThrownBy(() -> { 이호선.addSection(삼성역, 역삼역, 10); })
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("이미 등록되어 있는 지하철역 입니다.");
    }

    @DisplayName("노선에 구간을 추가할 때, 추가하는 구간의 길이가 1미만 일 수 없다.")
    @Test
    void addSection_distance_min() {
        assertThatThrownBy(() -> { 이호선.addSection(역삼역, 선릉역, 0); })
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("거리는 1 이하 일 수 없습니다.");
    }

    @DisplayName("노선에 포함된 지하철역을 조회한다.")
    @Test
    void getStations() {
        final List<Station> stations = 이호선.getStations();

        assertThat(stations).containsExactlyElementsOf(Arrays.asList(역삼역, 삼성역));
    }

    @DisplayName("노선의 구간이 하나일 때, 노선을 제거하면 오류가 발생한다.")
    @Test
    void removeSection_line_invalid_size() {
        assertThatThrownBy(() -> { 이호선.removeSection(삼성역); })
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("삭제할 수 없는 지하철 역 입니다.");
    }

    @DisplayName("노선의 끝의 구간을 제거한다.")
    @Test
    void removeSection_last() {
        이호선.addSection(삼성역, 봉은사역, 10);

        이호선.removeSection(봉은사역);

        assertThat(이호선.getStations()).containsExactlyElementsOf(Arrays.asList(역삼역, 삼성역));
    }

    @DisplayName("노선의 맨 앞의 구간을 제거한다.")
    @Test
    void removeSection_first() {
        이호선.addSection(삼성역, 봉은사역, 10);

        이호선.removeSection(역삼역);

        assertThat(이호선.getStations()).containsExactlyElementsOf(Arrays.asList(삼성역, 봉은사역));
    }

}
