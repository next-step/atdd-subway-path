package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("LineService Mock Test")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    /**
     * given stationRepository와 lineRepository stubbing 을 통해 초기값을 세팅하고
     * when lineService.addSection 호출한 후
     * then 불러온(line.getSections) 구간들에 추가한 구간이 포함되어 있다.
     */
    @DisplayName("지하철 노선 구간 추가")
    @Test
    void addSection() {
        // given
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        given(stationService.findById(2L)).willReturn(upStation);
        given(stationService.findById(3L)).willReturn(downStation);

        Line line = new Line("신분당선", "red");
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        lineService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // then
        assertThat(line.getSections()).isNotEmpty();
        assertThat(line.getSections()).contains(new Section(line, upStation, downStation, 10));
    }
}
