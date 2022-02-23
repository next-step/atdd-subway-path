package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
        Station 군자역 = new Station("군자역");
        Station 아차산역 = new Station("아차산역");
        Station 광나루역 = new Station("광나루역");

        long upStationId = 2L;
        long downStationId = 3L;
        int distance = 10;
        long lineId = 1L;
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        Line 오호선 = new Line("5호선", "보라색");
        오호선.getSections().add(Section.of(오호선, 군자역, 아차산역, request.getDistance()));

        LineService lineService = new LineService(lineRepository, stationService);
        given(stationService.findById(upStationId)).willReturn(아차산역);
        given(stationService.findById(downStationId)).willReturn(광나루역);
        given(lineRepository.findById(lineId)).willReturn(Optional.of(오호선));

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, request);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse response = lineService.findLine(lineId);
        assertThat(response.getStations()).hasSize(3);
    }
}
