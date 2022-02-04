package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private StationResponse 강남역_응답;
    private StationResponse 역삼역_응답;

    @BeforeEach
    void setUp() {
        강남역 = createStation(1L, "강남역");
        역삼역 = createStation(2L, "역삼역");
        선릉역 = createStation(3L, "선릉역");

        강남역_응답 = createStationsResponse(강남역);
        역삼역_응답 = createStationsResponse(역삼역);

        이호선 = createLine(1L , "이호선", "green");
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.createStationResponse(강남역)).thenReturn(강남역_응답);
        when(stationService.createStationResponse(역삼역)).thenReturn(역삼역_응답);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(이호선.getId());
        List<String> namesOfStations = lineResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertThat(namesOfStations).containsExactly(강남역.getName(), 역삼역.getName());

    }


    private Station createStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
    private Line createLine(Long id, String name, String color) {
        Line line = new Line(name, color);
        ReflectionTestUtils.setField(line, "id", id);
        return line;
    }

    private StationResponse createStationsResponse(Station station) {
        return new StationResponse(station.getId(), station.getName(), station.getCreatedDate(), station.getModifiedDate());
    }
}
