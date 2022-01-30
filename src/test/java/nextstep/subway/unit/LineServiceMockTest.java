package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private Station 강남역;
    private Station 정자역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        정자역 = new Station("정자역");
        신분당선 = new Line("신분당선", "red");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(정자역, "id", 2L);
        ReflectionTestUtils.setField(신분당선, "id", 1L);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);
        when(stationService.findById(강남역.getId()))
                .thenReturn(강남역);
        when(stationService.findById(정자역.getId()))
                .thenReturn(정자역);
        when(lineRepository.findById(신분당선.getId()))
                .thenReturn(Optional.ofNullable(신분당선));

        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 강남역.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 정자역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(신분당선.getId());
        assertThat(lineResponse.getId()).isEqualTo(신분당선.getId());
    }

}
