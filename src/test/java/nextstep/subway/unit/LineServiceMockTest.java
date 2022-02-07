package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService = new LineService(lineRepository, stationService);

    @Test
    void addSection() {
        // given
        Line line = new Line("2호선", "green");
        Station firstUpStation = new Station("신림역");
        Station firstDownStation = new Station("봉천역");
        Station newStation = new Station("서울대입구역");

        setField(line, "id", 1L);
        setField(firstUpStation, "id", 1L);
        setField(firstDownStation, "id", 2L);
        setField(newStation, "id", 3L);

        given(lineRepository.findById(line.getId())).willReturn(Optional.of(line));
        given(stationService.findById(firstUpStation.getId())).willReturn(firstUpStation);
        given(stationService.findById(firstDownStation.getId())).willReturn(firstDownStation);
        given(stationService.findById(newStation.getId())).willReturn(newStation);

        SectionRequest firstSectionRequest =
                new SectionRequest(firstUpStation.getId(), firstDownStation.getId(), 10);
        SectionRequest sectionRequest =
                new SectionRequest(firstDownStation.getId(), newStation.getId(), 10);

        // when
        lineService.addSection(line.getId(), firstSectionRequest);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        List<Long> downStationsIds = line.getSections()
                .stream()
                .map(s -> s.getDownStation().getId())
                .collect(Collectors.toList());
        assertThat(downStationsIds).contains(firstDownStation.getId(), newStation.getId());
    }
}
