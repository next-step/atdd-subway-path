package atdd.path.service;

import atdd.path.application.dto.EdgeRequestView;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
            .interval(INTERVAL_TIME)
            .build();
    public EdgeRequestView edgeRequestView = EdgeRequestView.builder()
            .sourceId(station1.getId())
            .targetId(station2.getId())
            .lineId(line.getId())
            .build();
    public Edge edge = Edge.builder()
            .id(1L)
            .lineId(1L)
            .sourceId(1L)
            .targetId(2L)
            .build();


    @InjectMocks
    EdgeService edgeService;

    @Mock
    EdgeRepository edgeRepository;

    @Test
    void 엣지를_추가한다() throws Exception {
        //given
        given(edgeRepository.save(any())).willReturn(edge);
        EdgeRequestView requestView = EdgeRequestView.builder()
                .sourceId(station1.getId())
                .targetId(station2.getId())
                .lineId(line.getId())
                .build();

        //when
        Edge edge2 = edgeService.createEdge(requestView);

        //then
        assertThat(edge2.getId()).isEqualTo(1L);
    }

    @Test
    void 엣지의_출발역과_도착역이_같으면_안_된다() {
        //given
        EdgeRequestView requestView = EdgeRequestView.builder()
                .sourceId(station1.getId())
                .targetId(station1.getId())
                .lineId(line.getId())
                .build();

        //when, then
        assertThrows(IllegalArgumentException.class, () -> {
            edgeService.createEdge(requestView);
        });
    }

    @Test
    void 엣지를_삭제한다() {
        //given
        given(edgeRepository.findById(edge.getId())).willReturn(Optional.of(edge));

        //when
        edgeService.deleteEdge(edge.getId());

        //then
        verify(edgeRepository, times(1)).deleteById(edge.getId());
    }

    @Test
    void 등록된_엣지만_삭제할_수_있다() {
        //given
        given(edgeRepository.findById(edge.getId())).willReturn(Optional.empty());

        //when, then
        assertThrows(NoSuchElementException.class, () -> {
            edgeService.deleteEdge(edge.getId());
        });
        verify(edgeRepository, times(0)).deleteById(any());
    }
}
