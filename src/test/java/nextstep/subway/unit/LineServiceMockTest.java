package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private static final int CREATE_LINE_DISTANCE = 10;

    private static final int ADD_SECTION_DISTANCE = 5;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선에 새로운 구간 추가하기")
    @Test
    void addSection() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("1호선", "남색")));
        when(stationService.findById(1L)).thenReturn(new Station("서울역"));
        when(stationService.findById(2L)).thenReturn(new Station("시청역"));

        // when
        lineService.addSection(1L, new SectionRequest(1L, 2L, CREATE_LINE_DISTANCE));

        // then
        Line line = lineService.findLineById(1L);

        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getColor()).isEqualTo("남색");
    }

    @DisplayName("지하철 노선에 구간 제거하기")
    @Test
    void deleteSection() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.of(new Line("1호선", "남색")));
        when(stationService.findById(1L)).thenReturn(new Station("서울역"));
        when(stationService.findById(2L)).thenReturn(new Station("시청역"));
        when(stationService.findById(3L)).thenReturn(new Station("종각역"));

        lineService.addSection(1L, new SectionRequest(1L, 2L, CREATE_LINE_DISTANCE));
        lineService.addSection(1L, new SectionRequest(2L, 3L, ADD_SECTION_DISTANCE));

        // when
        lineService.deleteSection(1L, 2L);

        // then
        List<Station> stations = lineService.findLineById(1L).getStations();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations).doesNotContain(new Station("시청역"));
    }
}
