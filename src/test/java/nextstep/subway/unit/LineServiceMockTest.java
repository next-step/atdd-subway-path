package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.section.SectionRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private final Long 이호선아이디 = 1L;
    private final Long 역삼역아이디 = 2L;
    private final Long 선릉역아이디 = 3L;

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);

        when(stationRepository.findById(역삼역아이디)).thenReturn(Optional.of(역삼역));
        when(stationRepository.findById(선릉역아이디)).thenReturn(Optional.of(선릉역));
        when(lineRepository.findById(이호선아이디)).thenReturn(Optional.of(이호선));

        // when
        SectionRequest sectionRequest = new SectionRequest(역삼역아이디, 선릉역아이디, 10L);
        lineService.addSection(이호선아이디, sectionRequest);

        // then
        LineResponse lineResponse = lineService.findLineById(이호선아이디);
        assertThat(lineResponse.getStations()).hasSize(3);
    }

}
