package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

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

    @Test
    @DisplayName("addSection 단위 테스트")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(stationService.findStationById(2L)).willReturn(역삼역);
        given(stationService.findStationById(3L)).willReturn(삼성역);
        given(lineRepository.findById(1L)).willReturn(Optional.ofNullable(이호선));

        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 6);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineService.findLineById(이호선.getId());
        assertThat(lineService.getStations(line)).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 삼성역));
    }
}
