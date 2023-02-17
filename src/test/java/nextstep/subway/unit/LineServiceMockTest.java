package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private Line 신분당선;
    private Station 미금역;
    private Station 판교역;
    private Long LINE_ID_신분당선;
    private Long STATION_ID_미금역;
    private Long STATION_ID_판교역;


    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        미금역 = new Station("미금역");
        판교역 = new Station("판교역");
        LINE_ID_신분당선 = 1L;
        STATION_ID_미금역 = 1L;
        STATION_ID_판교역 = 2L;
    }

    @Test
    @DisplayName("지하철 구간을 추가한다.")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        System.out.println("LINE_ID_신분당선 = " + LINE_ID_신분당선);
        given(lineRepository.findById(LINE_ID_신분당선)).willReturn(Optional.of(신분당선));
        given(stationService.findById(STATION_ID_미금역)).willReturn(미금역);
        given(stationService.findById(STATION_ID_판교역)).willReturn(판교역);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(STATION_ID_미금역, STATION_ID_판교역, 10);
        lineService.addSection(LINE_ID_신분당선, sectionRequest);

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(LINE_ID_신분당선);
        assertThat(line.getStations()).containsExactly(미금역, 판교역);
    }
}
