package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("지하철 노선에 역 등록 관련 기능 목 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Autowired
    private LineService lineService;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);

        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 1L);
        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 2L);
        삼성역 = new Station("삼성역");
        ReflectionTestUtils.setField(삼성역, "id", 3L);
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionToEnd() {
        // given
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findStationById(삼성역.getId())).thenReturn(삼성역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        // when
        lineService.addSection(이호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), 10));

        // then
        Line line = lineService.findLineById(이호선.getId());
        assertThat(line.getSections().size()).isEqualTo(2);
    }
}
