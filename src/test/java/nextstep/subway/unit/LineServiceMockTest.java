package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
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
        Station upStation = new Station("강남역");
        Station downStation = new Station("양재역");
        given(stationService.findById(2L)).willReturn(upStation);
        given(stationService.findById(3L)).willReturn(downStation);

        Line line = new Line("신분당선", "red");
        given(lineRepository.findById(1L)).willReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getSections()).isNotEmpty();
        assertThat(line.getSections()).contains(new Section(line, upStation, downStation, 10));
    }
}
