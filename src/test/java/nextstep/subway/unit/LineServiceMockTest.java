package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);

        Station 강남역 = new Station(1L, "강남역");
        Station 잠실역 = new Station(2L, "잠실역");
        int distance = 10;
        Line 이호선 = new Line(1L, "2호선", "green");

        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(잠실역);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));

        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 잠실역.getId(), distance);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineRepository.findById(이호선.getId()).get();
        assertThat(line.getSections().get(0).getUpStation().getName()).isEqualTo(강남역.getName());
        assertThat(line.getSections().get(0).getDownStation().getName()).isEqualTo(잠실역.getName());
        assertThat(line.getSections().get(0).getDistance()).isEqualTo(distance);
    }
}
