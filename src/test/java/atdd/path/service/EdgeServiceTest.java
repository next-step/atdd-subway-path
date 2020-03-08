package atdd.path.service;

import atdd.path.application.dto.EdgeRequestView;
import atdd.path.application.dto.EdgeResponseView;
import atdd.path.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EdgeServiceTest {
    public static final String LINE_2_NAME = "2호선";
    public static final String STATION_NAME = "사당";
    public static final String STATION_NAME_2 = "방배";
    public static final String STATION_NAME_3 = "서초";
    public static final String STATION_NAME_4 = "강남";
    public static final LocalTime START_TIME = LocalTime.of(5, 00);
    public static final LocalTime END_TIME = LocalTime.of(23, 50);
    public static final int INTERVAL_TIME = 10;
    public static final int DISTANCE_KM = 5;
    public Station station1 = Station.builder()
            .id(1L)
            .name(STATION_NAME)
            .build();
    public Station station2 = Station.builder()
            .id(2L)
            .name(STATION_NAME_2)
            .build();
    public Station station3 = Station.builder()
            .id(3L)
            .name(STATION_NAME_3)
            .build();
    public Station station4 = Station.builder()
            .id(4L)
            .name(STATION_NAME_4)
            .build();
    public Line line = Line.builder()
            .id(1L)
            .name(LINE_2_NAME)
            .startTime(START_TIME)
            .endTime(END_TIME)
            .intervalTime(INTERVAL_TIME)
            .build();
    public EdgeRequestView edgeRequestView = EdgeRequestView.builder()
            .source(station1)
            .target(station2)
            .line(line)
            .timeToTake(INTERVAL_TIME)
            .distance(DISTANCE_KM)
            .build();
    public EdgeRequestView edgeRequestView2 = EdgeRequestView.builder()
            .source(station2)
            .target(station3)
            .line(line)
            .timeToTake(INTERVAL_TIME)
            .distance(DISTANCE_KM)
            .build();
    public Edge edge = Edge.builder()
            .id(1L)
            .line(line)
            .source(station1)
            .target(station2)
            .distance(DISTANCE_KM)
            .timeToTake(INTERVAL_TIME)
            .build();
    public Edge edge2 = Edge.builder()
            .id(2L)
            .line(line)
            .source(station2)
            .target(station3)
            .build();
    public Edge newEdge = Edge.builder()
            .id(3L)
            .line(line)
            .source(edge.getSource())
            .target(edge2.getTarget())
            .distance(edge.getDistance()+edge2.getDistance())
            .timeToTake(edge.getTimeToTake()+edge2.getDistance())
            .build();

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
        given(edgeRepository.save(any())).willReturn(edge);

        //when
        EdgeResponseView edgeResponseView = edgeService.addEdge(edgeRequestView);

        //then
        assertThat(edgeResponseView.getSource().getEdgesAsSource().size()).isEqualTo(1);
        assertThat(edgeResponseView.getTarget().getEdgesAsTarget().size()).isEqualTo(1);
    }

    @Test
    void 지하철역_아이디를_주면_해당_역이_포함된_엣지를_삭제한다() throws Exception {
        //given
        line.addEdgeToLine(edge);
        station2.addEdgeToTarget(edge);
        station1.addEdgeToSource(edge);
        given(stationRepository.findById(anyLong()))
                .willReturn(Optional.of(station2));
        given(lineRepository.findById(any()))
                .willReturn(Optional.of(line));
//        given(edgeRepository.findById(anyLong()))
//                .willReturn(Optional.of(edge));

        //when
        edgeService.deleteEdgesByStationId(1L, station2.getId());

        //then
        verify(edgeRepository, times(1)).deleteById(edge.getId());
    }

    @Test
    void 지하철역이_삭제된_엣지아이디를_2개를_주면_병합한다() throws Exception{
        //given
        given(edgeRepository.findById(1L))
                .willReturn(Optional.of(edge));
        given(edgeRepository.findById(2L))
                .willReturn(Optional.of(edge2));
        given(edgeRepository.save(any()))
                .willReturn(newEdge);
        given(lineRepository.findById(1L))
                .willReturn(Optional.of(line));

        //when
        Edge newEdge = edgeService.mergeEdges(1L, this.edge.getId(), this.edge2.getId());

        //then
        assertThat(newEdge.getSource()).isEqualTo(edge.getSource());
        assertThat(newEdge.getTarget()).isEqualTo(edge2.getTarget());
    }
}
