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
public class LineServiceMockTest {
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
        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        선릉역 = new Station("선릉역");
        ReflectionTestUtils.setField(선릉역, "id", 3L);

        강남역_응답 = new StationResponse(강남역.getId(), 강남역.getName(), 강남역.getCreatedDate(), 강남역.getModifiedDate());
        역삼역_응답 = new StationResponse(역삼역.getId(), 역삼역.getName(), 역삼역.getCreatedDate(), 역삼역.getModifiedDate());

        이호선 = new Line("이호선", "green");
        ReflectionTestUtils.setField(이호선, "id", 1L);

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
}
