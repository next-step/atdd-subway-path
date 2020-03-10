package atdd.path.service;

import atdd.path.domain.Edge;
import atdd.path.domain.EdgeRepository;
import atdd.path.domain.Edges;
import atdd.path.domain.Line;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
    void 삭제하려는_지하철역의_소스와_타깃_새로운_엣지_병합된다(){
        //given
        given(lineService.findById(anyLong())).willReturn(TEST_LINE);
        given(stationService.findById(anyLong())).willReturn(TEST_STATION_2);
        given(edgeRepository.findById(1L)).willReturn(Optional.of(TEST_EDGE));
        given(edgeRepository.findById(2L)).willReturn(Optional.of(TEST_EDGE_2));

        //when
        edgeService.mergeEdgeByStationId(LINE_ID, STATION_ID_2);

        //then
        verify(edgeRepository, times(1)).save(any(Edge.class));
    }

    @Test
    void 지하철역을_삭제하면_해당역을_포함한_엣지도_삭제한다() {
        //given
        given(lineService.findById(anyLong())).willReturn(TEST_LINE);
        given(stationService.findById(anyLong())).willReturn(TEST_STATION_2);
        given(edgeRepository.findById(1L)).willReturn(Optional.of(TEST_EDGE));
        given(edgeRepository.findById(2L)).willReturn(Optional.of(TEST_EDGE_2));

        //when
        Edges edges = edgeService.deleteEdgeByStationId(LINE_ID, STATION_ID_2);

        //then
        verify(edgeRepository, times(2)).deleteById(anyLong());
        assertThat(edges.findTargetStation(TEST_STATION)).isEmpty();
        assertThat(edges.findSourceStation(TEST_STATION_3)).isEmpty();
    }
}