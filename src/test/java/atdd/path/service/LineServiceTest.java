package atdd.path.service;

import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.Line;
import atdd.path.domain.LineRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    public static final String LINE_BASE_URI = "/lines";
    public static final String LINE_2_NAME = "2호선";
    public static final LocalTime START_TIME = LocalTime.of(5, 00);
    public static final LocalTime END_TIME = LocalTime.of(23, 50);
    public static final int INTERVAL_TIME = 10;

    @InjectMocks
    LineService lineService;

    @Mock
    LineRepository lineRepository;

    @Test
    void 지하철노선_등록하기() {
        //given
        Line line = Line.builder()
                .id(1L)
                .name(LINE_2_NAME)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        LineRequestView requestView = LineRequestView.builder()
                .name(LINE_2_NAME)
                .startTime(START_TIME)
                .endTime(END_TIME)
                .interval(INTERVAL_TIME)
                .build();
        given(lineRepository.save(any(Line.class))).willReturn(line);

        //when
        LineResponseView responseView = lineService.create(requestView);

        //then
        assertThat(responseView.getName()).isEqualTo(LINE_2_NAME);
        assertThat(responseView.getEndTime()).isEqualTo(END_TIME);
    }
}
