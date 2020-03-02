package atdd.path.service;

import atdd.path.application.dto.LineListResponseView;
import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.*;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    public static final String LINE_2_NAME = "2호선";
    public static final String LINE_4_NAME = "2호선";
    public static final String STATION_NAME = "사당";
    public static final String STATION_NAME_2 = "방배";
    public static final String STATION_NAME_3 = "서초";
    public static final String STATION_NAME_4 = "강남";
    public static final LocalTime START_TIME = LocalTime.of(5, 00);
    public static final LocalTime END_TIME = LocalTime.of(23, 50);
    public static final int INTERVAL_TIME = 10;
    public Station station1 = Station.builder()
            .name(STATION_NAME)
            .build();
    public Station station2 = Station.builder()
            .name(STATION_NAME_2)
            .build();
    public Station station3 = Station.builder()
            .name(STATION_NAME_3)
            .build();
    public Station station4 = Station.builder()
            .name(STATION_NAME_4)
            .build();
    public Line line = Line.builder()
            .id(1L)
            .name(LINE_2_NAME)
            .startTime(START_TIME)
            .endTime(END_TIME)
            .interval(INTERVAL_TIME)
            .build();
    public Line line2 = Line.builder()
            .id(2L)
            .name(LINE_4_NAME)
            .startTime(START_TIME)
            .endTime(END_TIME)
            .interval(INTERVAL_TIME)
            .build();
    public LineRequestView requestView = LineRequestView.builder()
            .name(LINE_2_NAME)
            .startTime(START_TIME)
            .endTime(END_TIME)
            .interval(INTERVAL_TIME)
            .build();
    public Edge edge = Edge.builder()
            .lineId(line.getId())
            .sourceId(station1.getId())
            .targetId(station2.getId())
            .build();

    @InjectMocks
    LineService lineService;

    @Mock
    LineRepository lineRepository;

    @Test
    void 지하철노선_등록하기() {
        //given
        given(lineRepository.save(any(Line.class))).willReturn(line);

        //when
        LineResponseView responseView = lineService.create(requestView);

        //then
        assertThat(responseView.getName()).isEqualTo(LINE_2_NAME);
        assertThat(responseView.getEndTime()).isEqualTo(END_TIME);
    }

    @Test
    void 지하철노선의_시작시간은_종료시간보다_빨라야_한다() {
        //given
        LocalTime newStartTime = LocalTime.of(23, 55);
        line.changeStartTime(newStartTime);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> {
            lineService.create(LineRequestView.of(line));
        });
    }

    @Test
    void 지하철노선의_배차간격은_영보다_커야한다() {
        //given
        int newInterval = -2;
        line.changeInterval(newInterval);

        //when, then
        assertThrows(IllegalArgumentException.class, () -> {
            lineService.create(LineRequestView.of(line));
        });
    }

    @Test
    void 지하철노선을_삭제한다() {
        //given
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        //when
        lineService.delete(line.getId());

        //then
        verify(lineRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void 이미_등록된_지하철노선만_삭제_가능하다() {
        //given
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        lineService.delete(line.getId());

        //then
        verify(lineRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void 지하철_노선_정보_조회하기(){
        //given
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        //when
        LineResponseView responseView = lineService.retrieve(line.getId());

        //then
        assertThat(responseView.getId()).isEqualTo(line.getId());
        assertThat(responseView.getName()).isEqualTo(line.getName());
    }

    @Test
    void 등록된_노선만_조회_가능하다(){
        //given
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThrows(NoSuchEntityException.class, () -> {
            lineService.retrieve(line.getId());
        });
    }

    @Test
    void 노선_목록_조회하기(){
        //given
        int theNumberOfLine = 2;
        given(lineRepository.findAll()).willReturn(Arrays.asList(line, line2));

        //when
        LineListResponseView responseView = lineService.showAll();

        //then
        assertThat(responseView.getLines().size()).isEqualTo(theNumberOfLine);
    }
}
