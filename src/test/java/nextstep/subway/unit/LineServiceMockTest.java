package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        this.lineService = new LineService(lineRepository, stationService);
    }

    /**
     * scenario LineService#addSection 노선 id, 상행역 id, 하행역 id, 구간 거리 를 통해 노선에 구간을 등록한다
     * given lineRepository, stationService stub 설정을 통해 초기값 셋팅
     * when lineService.addSection 호출
     * then line.findLineById 메서드를 통해 검증
     */
    @Test
    void addSection() {
        // given
        Line 분당선 = new Line("신분당선", "red-100");

        when(lineRepository.findById(1L))
                .thenReturn(Optional.of(분당선));

        Station 강남역 = new Station("강남역");
        when(stationService.findById(1L))
                .thenReturn(강남역);

        Station 역삼역 = new Station("역삼역");
        when(stationService.findById(2L))
                .thenReturn(역삼역);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 10));

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineRepository.findById(1L).orElseThrow(RuntimeException::new);
        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
