package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private static final Long ONE_ID = 1L;
    private static final Long TWO_ID = 2L;
    private static final String LINE_ONE_NM = "일호선";
    private static final String LINE_ONE_COLOR = "blue";
    private static final String UP_STATION_NM_= "신도림";
    private static final String DOWN_STATION_NM = "가산디지털단지";
    private static final int DISTANCE_DEFAULT = 10;

    private final Line 일호선 = new Line(ONE_ID, LINE_ONE_NM, LINE_ONE_COLOR);
    private final Station 신도림 = new Station(ONE_ID, UP_STATION_NM_);
    private final Station 가산디지털단지 = new Station(TWO_ID, DOWN_STATION_NM);

    @Test
    @DisplayName("구간을 생성한다.")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        doReturn(Optional.of(일호선)).when(lineRepository).findById(ONE_ID);
        doReturn(신도림).when(stationService).findById(ONE_ID);
        doReturn(가산디지털단지).when(stationService).findById(TWO_ID);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(ONE_ID, TWO_ID, DISTANCE_DEFAULT);
        lineService.addSection(ONE_ID, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(일호선.getSections()).hasSize(1);
    }
}
