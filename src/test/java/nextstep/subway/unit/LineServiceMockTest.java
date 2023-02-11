package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@DisplayName("구간 서비스 mock 테스트")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    Line line;
    Station firstStation;
    Station secondStation;
    Section section;


    @BeforeEach
    void init() {
        line = new Line("신분당선", "red");
        firstStation = new Station("강남역");
        secondStation = new Station("판교역");
        section = new Section(line, firstStation, secondStation, 10);

        line.addSections(section);
    }

    @Test
    @DisplayName("지하철 구간을 추가할 수 있다.")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
        when(stationService.findById(1L)).thenReturn(firstStation);
        when(stationService.findById(2L)).thenReturn(secondStation);

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(1L);
        assertThat(lineResponse.getName()).isEqualTo(line.getName());
        assertThat(lineResponse.getStations()).hasSize(line.getStations().size());
    }
}
