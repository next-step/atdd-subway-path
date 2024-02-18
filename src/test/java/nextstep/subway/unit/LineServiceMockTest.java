package nextstep.subway.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Optional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineService;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @InjectMocks
    private LineService lineService;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() throws NoSuchFieldException, IllegalAccessException {
        // given
        // 리플렉션
        Field idField = Station.class.getDeclaredField("id");
        idField.setAccessible(true);
        Station upStation = new Station("상행역");
        idField.set(upStation, 1L);
        Station downStation = new Station("하행역");
        idField.set(downStation, 2L);

        // stub
        when(lineRepository.findById(any(Long.class))).thenReturn(Optional.of(new Line()));
        when(stationService.findById(1L)).thenReturn(upStation);
        when(stationService.findById(2L)).thenReturn(downStation);

        // when
        Line expectedLine = lineService.findLineById(1L);
        Station findUpStation = stationService.findById(1L);
        Station findDownStation = stationService.findById(2L);
        Section section = new Section(expectedLine, findUpStation, findDownStation, 10L);
        lineService.addSection(expectedLine, section);

        // then
        Line line = lineService.findLineById(1L);
        Assertions.assertThat(line.getSections()).hasSize(1);
    }
}
