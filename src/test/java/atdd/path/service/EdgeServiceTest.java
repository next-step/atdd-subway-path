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

import java.util.Optional;

import static atdd.path.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        //given
        given(lineRepository.findById(any())).willReturn(Optional.of(TEST_LINE));
        given(stationRepository.findById(STATION_ID)).willReturn(Optional.of(TEST_STATION));
        given(stationRepository.findById(STATION_ID_2)).willReturn(Optional.of(TEST_STATION_2));
        given(edgeRepository.save(any(Edge.class))).willReturn(TEST_EDGE);

        //when
        Edge edge = edgeService.addEdge(LINE_ID, STATION_ID, STATION_ID_2, 10);

        //then
        verify(edgeRepository, times(1)).save(any(Edge.class));
        assertThat(edge.getId()).isEqualTo(1L);
        assertThat(edge.getSource().getId()).isEqualTo(STATION_ID);
        assertThat(edge.getTarget().getId()).isEqualTo(STATION_ID_2);
    }
}
