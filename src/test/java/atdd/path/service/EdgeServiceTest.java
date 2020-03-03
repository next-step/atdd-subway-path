package atdd.path.service;

import atdd.path.application.dto.EdgeRequestView;
import atdd.path.application.dto.EdgeResponseView;
import atdd.path.domain.Edge;
import atdd.path.domain.EdgeRepository;
import atdd.path.domain.Line;
import atdd.path.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

    @InjectMocks
    EdgeService edgeService;

    @Mock
    EdgeRepository edgeRepository;

    @Test
    void 엣지를_추가한다() throws Exception {
        //given
        given(edgeRepository.save(any())).willReturn(edge);

        //when
        EdgeResponseView edgeResponseView = edgeService.addEdge(edgeRequestView);

        //then
        assertThat(edgeResponseView.getSource().getEdgesAsSource().size()).isEqualTo(1);
        assertThat(edgeResponseView.getTarget().getEdgesAsTarget().size()).isEqualTo(1);
        assertThat(edgeResponseView.getSource().getLines().size()).isEqualTo(1);
        assertThat(edgeResponseView.getTarget().getLines().size()).isEqualTo(1);
    }
}
