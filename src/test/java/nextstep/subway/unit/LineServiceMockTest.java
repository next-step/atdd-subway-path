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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Line line = new Line("2호선", "bg-green-600");
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 3);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
    }
}
