package nextstep.subway.unit;

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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    private final Long lineId = 10L;
    private final Line line = new Line("신분당선", "red");
    private final Station upStation = new Station("양재역");
    private final Station downStation = new Station("판교역");

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private SectionRequest request;

    // given
    @BeforeEach
    void init() {
        request = setUpStubs(lineId);
    }

    @Test
    void addSection() {
        // when
        // lineService.addSection 호출
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.addSection(lineId, request);

        // then
        // lineService.findLineById 메서드를 통해 검증
        Line line = lineService.findById(lineId);
        List<String> stationNames = line.getStations().stream().map(Station::getName).collect(Collectors.toList());

        assertThat(line.getSectionsCount()).isEqualTo(1);
        assertThat(stationNames).contains(upStation.getName(), downStation.getName());
    }

    private SectionRequest setUpStubs(Long lineId) {
        SectionRequest request = new SectionRequest(1L, 2L, 10);

        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        when(stationService.findById(request.getUpStationId())).thenReturn(upStation);
        when(stationService.findById(request.getDownStationId())).thenReturn(downStation);

        return request;
    }

}
