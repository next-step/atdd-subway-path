package atdd.path.service;

import atdd.path.domain.Edge;
import atdd.path.domain.EdgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static atdd.path.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    LineService lineService;

    @Mock
    StationService stationService;

    @Test
    void 엣지를_추가한다() {
        //given
        given(lineService.findById(any())).willReturn(TEST_LINE);
        given(stationService.findById(STATION_ID)).willReturn(TEST_STATION);
        given(stationService.findById(STATION_ID_2)).willReturn(TEST_STATION_2);
        given(edgeRepository.save(any(Edge.class))).willReturn(TEST_EDGE);

        //when
        Edge edge = edgeService.addEdge(LINE_ID, STATION_ID, STATION_ID_2, 10);

        //then
        verify(edgeRepository, times(1))
                .save(any(Edge.class));
        assertThat(edge.getId())
                .isEqualTo(1L);
        assertThat(edge.getSource().getId())
                .isEqualTo(STATION_ID);
        assertThat(edge.getTarget().getId())
                .isEqualTo(STATION_ID_2);
    }

    @Test
    void 지하철역을_삭제하면_해당역을_포함한_엣지도_삭제한다() {
        //given
        given(lineService.findById(anyLong())).willReturn(TEST_LINE);
        given(stationService.findById(anyLong())).willReturn(TEST_STATION_2);


        //when
        edgeService.deleteEdgeByStationId(LINE_ID, STATION_ID_2);

        //then
        verify(edgeRepository, times(2))
                .deleteById(anyLong());
    }
}
