package nextstep.subway.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    LineService sut;

    @BeforeEach
    void setUp() {
        sut = new LineService(lineRepository, stationService);
    }

    @Test
    void addSection() {
        // given
        Long lineId = 1L;
        Station 봉천역 = new Station(1L, "봉천역");
        Station 신림역 = new Station(2L, "신림역");

        SectionRequest request = SectionRequest.builder()
            .upStationId(봉천역.getId())
            .downStationId(신림역.getId())
            .distance(10)
            .build();

        Line line = Mockito.mock(Line.class);
        when(stationService.findById(봉천역.getId())).thenReturn(봉천역);
        when(stationService.findById(신림역.getId())).thenReturn(신림역);
        when(lineRepository.findById(lineId)).thenReturn(Optional.ofNullable(line));

        // when
        sut.addSection(lineId, request);

        // then
        verify(line).addSection(any());
    }
}
