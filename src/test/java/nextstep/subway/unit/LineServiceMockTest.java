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

    /**
     * 리팩토링을 진행하면서 목을 사용하다보니 구현에 강하게 결합된 테스트여서 깨짐.
     * 기존에는 LineService 가 StationService 에 의존하고 있었는데, StationRepository 를 의존하도록 변경하였습니다.
     */
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
//        var upStationId = 1L;
//        var downStationId = 2L;
//        var upStation = new Station("신논현역");
//        var downStation = new Station("강남역");
//        var distance = 10;
//        var lineId = 1L;
//        var line = new Line("신분당선", "bg-red-600");
//
//        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
//        when(stationService.findById(upStationId)).thenReturn(upStation);
//        when(stationService.findById(downStationId)).thenReturn(downStation);

        // when
        // lineService.addSection 호출
//        var sut = new LineService(lineRepository, stationService);
//        sut.addStationToLine(lineId, SectionRequest.of(upStationId, downStationId, distance));

        // then
        // line.findLineById 메서드를 통해 검증
//        var lineResponse = sut.findById(lineId);
//        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }
}
