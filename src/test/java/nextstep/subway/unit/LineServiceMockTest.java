package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@DisplayName("구간 서비스 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선에 구간 추가")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 강남역 = new Station("강남역");
        Station 양재역 = new Station("양재역");
        Line 이호선 = new Line("2호선", "green", 강남역, 양재역, 7);

        // when
        Station 구디역 = new Station("구디역");
        SectionRequest sectionRequest = new SectionRequest(구디역.getId(), 강남역.getId(), 3);

        Mockito.when(stationService.findById(any()))
                .thenReturn(구디역)
                .thenReturn(강남역);
        Mockito.when(lineRepository.findById(이호선.getId()))
                .thenReturn(Optional.of(이호선));

        // then
        Line line = lineService.addSection(이호선.getId(), sectionRequest);
        Assertions.assertThat(line.getSections().size()).isEqualTo(2);
    }
}