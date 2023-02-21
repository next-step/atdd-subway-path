package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.unit.fixture.StationFixture.광교;
import static nextstep.subway.unit.fixture.StationFixture.광교중앙;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        Line 신분당선 = new Line(1L, "신분당선", "RED");
        when(stationService.findById(광교.getId())).thenReturn(광교);
        when(stationService.findById(광교중앙.getId())).thenReturn(광교중앙);
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(광교.getId(), 광교중앙.getId(), 5));

        // then
        assertThat(신분당선.getStations()).containsExactly(광교, 광교중앙);
    }
}
