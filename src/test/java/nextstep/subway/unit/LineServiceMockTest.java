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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        LineService lineService = new LineService(lineRepository, stationService);
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Station station3 = new Station(3L, "station3");
        Line line = new Line(1L, "1호선", "남색");
        int distance = 10;
        line.addSection(Section.of(line, station1, station2, distance));
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationService.findById(station2.getId())).thenReturn(station2);
        when(stationService.findById(station3.getId())).thenReturn(station3);
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), distance);

        // when
        lineService.addSection(line.getId(), sectionRequest);

        // then
        assertThat(lineService.findLineById(line.getId()).getStations()).containsExactly(station1, station2, station3);
    }
}
