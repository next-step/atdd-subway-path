package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.sections.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("구간 서비스 단위 테스트; mocking")
@Transactional
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    private SectionRequest sectionRequest = mock(SectionRequest.class);
    private Line 이호선;
    private Station 강남역;
    private Station 삼성역;

    @BeforeEach
    void setUp() {
        // Given
        when(sectionRequest.getUpStationId()).thenReturn(1L);
        when(sectionRequest.getDownStationId()).thenReturn(2L);
        when(sectionRequest.getDistance()).thenReturn(10);

        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        이호선 = new Line("2호선", "green");
    }

    @DisplayName("지하철노선의 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));
        when(stationService.findById(1L)).thenReturn(강남역);
        when(stationService.findById(2L)).thenReturn(삼성역);

        // when
        lineService.addSection(1L, sectionRequest);

        // then
        Sections sections = lineService.findLineById(1L)
                .getSections();
        assertThat(sections.size()).isEqualTo(1);
    }
}
