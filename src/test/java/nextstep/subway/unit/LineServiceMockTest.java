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

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.anyLong;
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
        Long lineId = 3L;
        Long upStationId = 1L;
        Long downStationId = 2L;

        given(stationService.findById(upStationId)).willReturn(new Station("강남역"));
        given(stationService.findById(downStationId)).willReturn(new Station("건대입구역"));
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(new Line("2호선", "green")));

        SectionRequest request = new SectionRequest(upStationId, downStationId, 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, request);

        // then
        // line.findLineById 메서드를 통해 검증
        Line findLine = lineRepository.findById(lineId).get();
        then(findLine.getSections()).hasSize(1);
    }
}
