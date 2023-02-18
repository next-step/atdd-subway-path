package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    private static final String SEOUL_STATION = "서울역";
    private static final String YONGSAN_STATION = "용산역";
    private static final String LINE_ONE = "1호선";
    private static final String BACKGROUND_COLOR_BLUE = "bg-color-blue";
    private static final int DISTANCE_FIVE = 5;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(stationService.findById(any())).willReturn(new Station(SEOUL_STATION))
                .willReturn(new Station(YONGSAN_STATION));
        given(lineRepository.findById(any())).willReturn(Optional.of(new Line(LINE_ONE, BACKGROUND_COLOR_BLUE)));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, DISTANCE_FIVE));

        // then
        // line.findLineById 메서드를 통해 검증
        Assertions.assertThat(lineService.findLineById(1L).getSections().getSections().size()).isEqualTo(1);
        Assertions.assertThat(lineService.findLineById(1L).getStations().size()).isEqualTo(2);
    }
}
