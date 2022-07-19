package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    private Station 영등포역;
    private Station 신도림역;
    private Station 구로역;
    private Line 일호선;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);

        영등포역 = new Station("영등포역");
        신도림역 = new Station("신도림역");
        구로역 = new Station("구로역");
        ReflectionTestUtils.setField(영등포역, "id", 1L);
        ReflectionTestUtils.setField(신도림역, "id", 2L);
        ReflectionTestUtils.setField(구로역, "id", 3L);

        일호선 = new Line("1호선", "blue");
        ReflectionTestUtils.setField(일호선, "id", 1L);
    }

    @Test
    void addSection() {
        // given
        when(lineRepository.findById(일호선.getId())).thenReturn(Optional.of(일호선));
        when(stationService.findById(영등포역.getId())).thenReturn(영등포역);
        when(stationService.findById(신도림역.getId())).thenReturn(신도림역);

        // when
        lineService.addSection(1L, new SectionRequest(1L, 2L, 20));

        // then
        Line line = lineService.findLineById(일호선.getId());
        assertAll(
                () -> assertThat(line.getSections().size()).isEqualTo(1),
                () -> assertThat(line.getStations()).contains(영등포역, 신도림역)
        );
    }
}
