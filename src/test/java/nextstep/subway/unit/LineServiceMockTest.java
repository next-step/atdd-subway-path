package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @InjectMocks
    private LineService lineService;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(lineRepository.findById(1L)).willReturn(Optional.of(new Line("8호선", "bg-pink-500")));
        given(stationService.findById(1L)).willReturn(new Station("암사역"));
        given(stationService.findById(2L)).willReturn(new Station("모란역"));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(1L, 2L, 17));

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(lineService.findById(1L).getStations()).hasSize(2);
    }
}
