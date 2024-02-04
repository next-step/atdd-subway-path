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
    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;
    private Line 강남_선릉_노선;

    @BeforeEach
    void init() {
        강남역 = new Station(1L, "강남역");
        선릉역 = new Station(2L, "선릉역");
        삼성역 = new Station(3L, "삼성역");
        강남_선릉_노선 = new Line(1L, "노선", "red", 강남역, 선릉역, 10);
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        강남_선릉_노선.addSection(선릉역, 삼성역, 10);

        assertThat(강남_선릉_노선.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 선릉역, 삼성역));
    }

    @DisplayName("노선의 하행종점역과 등록하려는 구간의 상행역이 다를 경우 예외가 발생한다.")
    @Test
    void addSection_invalid() {

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 강남_선릉_노선.addSection(삼성역, 삼성역, 10));
    }

    @DisplayName("노선에 포함된 지하철역을 조회한다.")
    @Test
    void getStations() {
        final List<Station> stations = 강남_선릉_노선.getStations();

        assertThat(stations).containsExactlyElementsOf(Arrays.asList(강남역, 선릉역));
    }

    @DisplayName("노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        강남_선릉_노선.addSection(선릉역, 삼성역, 10);

        강남_선릉_노선.removeSection(삼성역.getId());

        assertThat(강남_선릉_노선.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 선릉역));
    }

    @DisplayName("삭제하려는 노선의 구간이 노선의 하행종점역이 아니면 오류가 발생한다.")
    @Test
    void removeSection_invalid_downStationId() {
        강남_선릉_노선.addSection(선릉역, 삼성역, 10);

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 강남_선릉_노선.removeSection(선릉역.getId()));
    }

    @DisplayName("노선의 구간이 하나일 때, 노선을 제거하면 오류가 발생한다.")
    @Test
    void removeSection_line_invalid_size() {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> 강남_선릉_노선.removeSection(삼성역.getId()));
    }

}
