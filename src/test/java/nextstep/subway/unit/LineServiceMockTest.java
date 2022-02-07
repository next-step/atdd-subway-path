package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService = null;

    @BeforeEach
    public void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Long lineId = 1L;
        Long upStationId = 2L;
        Long downStationId = 3L;
        Station upStation = new Station(upStationId, "사당역");
        Station downStation = new Station(downStationId, "신도림역");

        Line line = 지하철_라인_역_샘플();
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        when(stationService.findById(upStationId)).thenReturn(upStation);
        when(stationService.findById(downStationId)).thenReturn(downStation);

        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 40);

        // when
        lineService.addSection(lineId, sectionRequest);

        // then
        // addSection이 적용되어 역이 2개에서 3개로 증가
        assertThat(line.getStations()).hasSize(3);
    }

    @Test
    @DisplayName("노선 정보 조회")
    void findById() {
        // given
        Line line = 지하철_라인_역_샘플();
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        // when
        LineResponse lineResponse = lineService.findById(line.getId());

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getStations()).hasSize(2)
        );
    }

    private Line 지하철_라인_역_샘플() {
        Station upStation = new Station(1, "강남역");
        Station downStation = new Station(2, "사당역");
        Line line = new Line("2호선", "green");
        line.addSection(upStation, downStation, 30);
        return line;
    }
}
