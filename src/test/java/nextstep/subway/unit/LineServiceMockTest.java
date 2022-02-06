package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.unit.SetupList.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
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
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));
        when(lineRepository.save(이호선)).thenReturn(이호선);


        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 3);
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse existLine = lineService.findById(이호선.getId());
        assertThat(existLine.getStations().size()).isEqualTo(2);
    }
}
