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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.fixture.LineFixture.createLine;
import static nextstep.subway.fixture.SectionFixture.createSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
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
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line line = new Line("name", "red");
        Station upStation = new Station("상행역");
        Station downStation = new Station("하행역");

        when(lineRepository.findById(any())).thenReturn(Optional.of(line));
        when(stationService.findById(1L)).thenReturn(upStation);
        when(stationService.findById(2L)).thenReturn(downStation);

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        lineService.addSection(1L, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getSections().get(0).getLine()).isEqualTo(line);
    }

    @Test
    void deleteSection() {
        long downStationId = 2L;
        Line line = createLine();
        line.addSection(createSection(0L, 1L));
        line.addSection(createSection(1L, downStationId));

        Section section = line.getSections().getLast();

        when(lineRepository.findById(any())).thenReturn(Optional.of(line));
        when(stationService.findById(downStationId)).thenReturn(section.getDownStation());

        lineService.deleteSection(1L, downStationId);

        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
