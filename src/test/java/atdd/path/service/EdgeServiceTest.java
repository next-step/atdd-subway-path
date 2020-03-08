package atdd.path.service;

import atdd.path.domain.Edge;
import atdd.path.domain.EdgeRepository;
import atdd.path.domain.LineRepository;
import atdd.path.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static atdd.path.TestConstant.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EdgeServiceTest {
    @InjectMocks
    EdgeService edgeService;

    @Mock
    EdgeRepository edgeRepository;

    @Mock
    StationRepository stationRepository;

    @Mock
    LineRepository lineRepository;

    @Test
    void 엣지를_추가한다() throws Exception {
        //when
        edgeService.addEdge(LINE_ID, STATION_ID, STATION_ID_2, 10);

        //then
        verify(edgeRepository, times(1)).save(any(Edge.class));
    }
}
