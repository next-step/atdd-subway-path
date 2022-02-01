package nextstep.subway.unit;

import nextstep.subway.applicaion.command.LineCommandService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.query.LineQueryService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private LineQueryService lineQueryService;

    private Station 강남역;
    private Station 판교역;
    private Station 정자역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = Station.of("강남역");
        판교역 = Station.of("판교역");
        정자역 = Station.of("정자역");
        신분당선 = Line.of("신분당선", "red", 강남역, 정자역, 100);
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(판교역, "id", 2L);
        ReflectionTestUtils.setField(정자역, "id", 3L);
        ReflectionTestUtils.setField(신분당선, "id", 1L);
    }

    @Test
    void addSection() {
        // given
        LineCommandService lineCommandService = new LineCommandService(lineRepository, lineQueryService, stationRepository);
        when(lineQueryService.findLineById(anyLong())).thenReturn(신분당선);

        when(stationRepository.findById(판교역.getId())).thenReturn(Optional.ofNullable(판교역));
        when(stationRepository.findById(정자역.getId())).thenReturn(Optional.ofNullable(정자역));

        SectionRequest sectionRequest = SectionRequest.of(판교역.getId(), 정자역.getId(), 10);

        // when
        lineCommandService.addSection(신분당선.getId(), sectionRequest);

        // then
        Line line = lineQueryService.findLineById(신분당선.getId());
        assertThat(line.getId()).isEqualTo(신분당선.getId());
    }

}
