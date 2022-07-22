package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.dto.PathResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @InjectMocks
    private PathService pathService;

    @Mock
    private LineService lineService;
    @Mock
    private StationService stationService;

    @Test
    void 최단경로조회성공() {
        final long source = 1L;
        final long target = 2L;

        final Station station1 = station(1);
        final Station station2 = station(2);

        doReturn(lines(station1, station2))
                .when(lineService)
                .findLines();

        doReturn(station1)
                .when(stationService)
                .findById(source);

        doReturn(station2)
                .when(stationService)
                .findById(target);

        final PathResponse response = pathService.showPath(source, target);
        assertThat(response).isNotNull();
    }

    private List<Line> lines(final Station station1, final Station station2) {
        final Line line = new Line();
        line.addSection(section(station1, station2));
        return Collections.singletonList(line);
    }
}