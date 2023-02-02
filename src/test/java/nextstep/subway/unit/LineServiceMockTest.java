package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    private Line 분당선;
    private Station 수서역;
    private Station 복정역;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);

        분당선 = new Line(1L, "분당선", "yellow");
        수서역 = new Station(1L, "수서역");
        복정역 = new Station(2L, "복정역");
    }

    @DisplayName("새로운 지하철 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);

        // when
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // then
        LineResponse response = lineService.findById(분당선.getId());
        assertThat(response.getStations()).hasSize(2);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        when(lineRepository.findAll()).thenReturn(Collections.singletonList(분당선));

        // when
        List<LineResponse> response = lineService.showLines();

        // then
        assertThat(response).hasSize(1);
    }
}
