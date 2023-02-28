package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {

    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    @DisplayName("")
    @Test
    void findPath() {
        //Given : lineService, stationService를 stubbing을 통한 초기값 세팅
        PathService pathService = new PathService(lineService, stationService);
        Line 이호선 = mock(Line.class);
        Line 신분당선 = mock(Line.class);
        Line 삼호선 = mock(Line.class);
        lineService.addSection(1L, new SectionRequest(1L,2L,10));
        lineService.addSection(2L, new SectionRequest(2L, 3L, 10));
        lineService.addSection(3L, new SectionRequest(1L, 4L, 2));
        lineService.addSection(3L, new SectionRequest(4L, 3L, 3));

        when(lineService.findAllLine()).thenReturn(List.of(이호선, 신분당선, 삼호선));
        when(stationService.findById(1L)).thenReturn(new Station("교대역"));
        when(stationService.findById(2L)).thenReturn(new Station("강남역"));
        when(stationService.findById(3L)).thenReturn( new Station("양재역"));
        when(stationService.findById(4L)).thenReturn(new Station("남부터미널역"));

        //When :
        PathResponse response = pathService.findPath(1L, 3L);

        //Then
        assertThat(response.getDistance()).isEqualTo(5);
    }
}
