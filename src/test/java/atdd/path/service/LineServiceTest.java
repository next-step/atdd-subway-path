package atdd.path.service;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.Line;
import atdd.path.domain.LineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalTime;
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
    public static final LocalTime START_TIME = LocalTime.of(5, 00);
    public static final LocalTime END_TIME = LocalTime.of(23, 50);
    public static final int INTERVAL_TIME = 10;
    public Line line = Line.builder()
            .id(1L)
            .name(LINE_2_NAME)
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

    @InjectMocks
    LineService lineService;

    @Mock
    LineRepository lineRepository;

    @Test
    void 지하철노선_등록하기() {
        //given
        given(lineRepository.save(any(Line.class))).willReturn(line);
        given(lineRepository.findByName(anyString())).willReturn(Optional.empty());

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
        given(lineRepository.findByName(anyString())).willReturn(Optional.empty());

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
        given(lineRepository.findByName(anyString())).willReturn(Optional.empty());

        //when, then
        assertThrows(IllegalArgumentException.class, () -> {
            lineService.create(LineRequestView.of(line));
        });
    }

    @Test
    void 이미_등록된_노선은_다시_등록할_수_없다() {
        //given
        given(lineRepository.findByName(anyString())).willReturn(Optional.of(line));

        //when, then
        assertThrows(EntityExistsException.class, () -> {
            lineService.create(LineRequestView.of(line));
        });
    }

    @Test
    void 지하철노선을_삭제한다(){
        //given
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        //when
        lineService.delete(line.getId());

        //then
        verify(lineRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void 이미_등록된_지하철노선만_삭제_가능하다(){
        //given
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        lineService.delete(line.getId());

        //then
        verify(lineRepository, times(0)).deleteById(anyLong());
    }
}
