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

    final Station 강남역 = Station.of("강남역");
    final Station 역삼역 = Station.of("역삼역");
    final Station 합정역 = Station.of("합정역");

    final Line 이호선 = Line.of("2호선", "green", 강남역, 역삼역, 100);

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        ReflectionTestUtils.setField(합정역, "id", 3L);

        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @Test
    void addSection() {
        final int 거리 = 50;

        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));

        when(stationService.findById(2L)).thenReturn(역삼역);
        when(stationService.findById(3L)).thenReturn(합정역);

        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationService);

        SectionRequest sectionRequest = new SectionRequest();
        ReflectionTestUtils.setField(sectionRequest, "upStationId", 역삼역.getId());
        ReflectionTestUtils.setField(sectionRequest, "downStationId", 합정역.getId());
        ReflectionTestUtils.setField(sectionRequest, "distance", 거리);

        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(이호선.getId());

        assertThat(lineResponse.getStations().size()).isEqualTo(3);
    }
}
