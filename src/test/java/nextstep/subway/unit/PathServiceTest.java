package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;

import nextstep.subway.domain.PathFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PathServiceTest {

    @Mock
    private LineService lineService;

    @Mock
    private StationService stationService;

    @DisplayName("")
    @Test
    void findPath() {
        //Given
        //lineService, stationService를 stubbing을 통한 초기값 세팅
        PathService pathService = new PathService(lineService, stationService);
        when(lineService.findAllLine()).thenReturn(any());
        when(stationService.findById(any())).thenReturn(any());
        PathFinder pathFinder = mock(PathFinder.class);

        //When
        //pathService.findPath() 호출
        pathService.findPath(any(), any());

        //Then
        //pathFinder.findPath() 호출됐는지 확인
        verify(pathFinder).findPath(any(),any());
    }
}
