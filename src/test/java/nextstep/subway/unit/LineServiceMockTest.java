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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선 마지막 역과 연결된 새로운 구간 등록")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        final Station upStation = new Station("upStation");
        final Station downStation = new Station("downStation");
        final Station extraStation = new Station("extraStation");
        final Line line = new Line("name", "color");
        line.addSection(new Section(line, upStation, downStation, 10));

        given(stationService.findById(2L)).willReturn(downStation);
        given(stationService.findById(3L)).willReturn(extraStation);
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(2L, 3L, 1));

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(upStation, downStation, extraStation);
    }
}
