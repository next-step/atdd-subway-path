package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.unit.SetupList.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

  private final static Long 이호선ID = 1L;
  private final static Long 강남역ID = 1L;
  private final static Long 역삼역ID = 2L;

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
    when(stationService.findById(강남역ID)).thenReturn(강남역);
    when(stationService.findById(역삼역ID)).thenReturn(역삼역);
    when(lineRepository.findById(이호선ID)).thenReturn(Optional.of(이호선));
    SectionRequest sectionRequest = new SectionRequest(강남역ID, 역삼역ID, 3);

    // when
    // lineService.addSection 호출
    lineService.addSection(이호선ID, sectionRequest);

    // then
    // line.findLineById 메서드를 통해 검증
    LineResponse response = lineService.findById(이호선ID);
    org.junit.jupiter.api.Assertions.assertAll(
      () -> assertThat(response.getName()).isEqualTo(이호선.getName()),
      () -> assertThat(response.getStations().size()).isEqualTo(2)
    );
  }
}
