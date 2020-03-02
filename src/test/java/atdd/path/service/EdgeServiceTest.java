package atdd.path.service;

import atdd.path.application.dto.EdgeRequestView;
import atdd.path.application.dto.LineRequestView;
import atdd.path.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void 엣지의_출발역과_도착역이_같으면_안_된다(){
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
}
