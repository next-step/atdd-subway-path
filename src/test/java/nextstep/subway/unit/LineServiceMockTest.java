package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);
        Line line = new Line(1L,"간선", "blue");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
        Station 수원역 = new Station(1L, "수원역");
        Station 수원중앙역 = new Station(2L, "수원중앙역");
        when(stationService.findById(1L)).thenReturn(new Station(1L,"수원역"));
        when(stationService.findById(2L)).thenReturn(new Station(2L, "수원중앙역"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(수원역, 수원중앙역);
    }
}
