package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    public static String 강남역_이름 = "강남역";
    public static String 분당역_이름 = "분당역";
    public static String 정자역_이름 = "정자역";
    public static String 신분당선_이름 = "신분당선";
    public static String 신분당선_색 = "Green";

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);
        when(stationService.findById(1L)).thenReturn(new Station(강남역_이름));
        when(stationService.findById(2L)).thenReturn(new Station(분당역_이름));
        Line line = mock(Line.class);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L,2L,10));

        // Line에 add가 호출 되었는지 검증
        verify(line).addSection(any(), any(), anyInt());
    }

    @Test
    void deleteSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);
        when(stationService.findById(2L)).thenReturn(new Station(분당역_이름));
        Line line = mock(Line.class);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(1L, 2L);

        // Line에 removeSection가 호출 되었는지 검증
        verify(line).removeSection(any());
    }
}
