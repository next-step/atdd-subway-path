package nextstep.subway.unit;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        final Line line = new Line(1L, "이호선", "초록색");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.createStationResponses(Set.of(강남역.getId(), 역삼역.getId())))
            .thenReturn(
                List.of(
                    createStationResponse(강남역),
                    createStationResponse(역삼역)
                )
            );

        // when
        final SectionRequest sectionRequest = new SectionRequest(
            강남역.getId(),
            역삼역.getId(),
            10
        );
        lineService.addSection(line.getId(), sectionRequest);

        // then
        final LineResponse lineResponse = lineService.findById(line.getId());
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(
            station.getId(),
            station.getName()
        );
    }
}
