package nextstep.subway.path.application;

import nextstep.subway.line.application.SectionService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.applicaion.StationService;
import nextstep.subway.station.applicaion.dto.response.StationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {
    private static final Line LINE = new Line("2호선", "green");

    @Mock
    private StationService stationService;

    @Mock
    private SectionService sectionService;

    @InjectMocks
    private PathService pathService;

    @Test
    void findPath() {
        // given
        when(stationService.findAllStations()).thenReturn(List.of(
                new StationResponse(1L, "교대역"),
                new StationResponse(2L, "강남역"),
                new StationResponse(3L ,"서초역")));

        when(sectionService.findAllSections()).thenReturn(List.of(
                        new Section(LINE, 1L, 2L, 2),
                        new Section(LINE, 2L, 3L, 2),
                        new Section(LINE, 1L, 3L, 20)));

        // when
        PathResponse path = pathService.findPath(1L, 3L);

        // then
        assertThat(path.getDistance()).isEqualTo(4);
        assertThat(path.getStations()).extracting("name")
                .containsExactly("교대역", "강남역", "서초역");
    }
}
