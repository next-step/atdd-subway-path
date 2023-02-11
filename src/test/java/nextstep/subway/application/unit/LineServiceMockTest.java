package nextstep.subway.application.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nextstep.subway.SubwayFixture.노선_생성;
import static nextstep.subway.SubwayFixture.역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    void addSection() {
        // given
        Station 수원역 = 역_생성(1L, "수원역");
        Station 매탄권선역 = 역_생성(2L, "매탄권선역");
        Line 분당선 = 노선_생성(1L, "분당선", "red");
        when(stationService.findById(수원역.getId())).thenReturn(수원역);
        when(stationService.findById(매탄권선역.getId())).thenReturn(매탄권선역);
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));

        // when
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 매탄권선역.getId(), 10);
        lineService.addSection(분당선.getId(), sectionRequest);

        // then
        verify(stationService, times(2)).findById(anyLong());
        verify(lineRepository, times(1)).findById(anyLong());
        assertThat(분당선.getSections()).hasSize(1)
                .flatExtracting(Section::getStations).hasSize(2)
                .containsAnyElementsOf(분당선.getStations())
                .extracting(Station::getName)
                .containsExactlyInAnyOrder("수원역", "매탄권선역");
    }
}
