package nextstep.subway.unit;
import static nextstep.subway.unit.PathFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {
    @Mock
    private StationService stationService;

    @Mock
    private LineRepository lineRepository;

    @InjectMocks
    private PathService pathService;

    @Test
    void searchPath() {
        // given
        given(stationService.findById(강남역.getId())).willReturn(강남역);
        given(stationService.findById(남부터미널역.getId())).willReturn(남부터미널역);
        given(lineRepository.findAll()).willReturn(노선_목록);

        // when
        PathResponse pathResponse = pathService.searchPath(강남역.getId(), 남부터미널역.getId());

        // then
        List<Long> stationIdList = pathResponse.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(stationIdList).containsExactly(강남역.getId(), 양재역.getId(), 남부터미널역.getId());
        assertThat(pathResponse.getDistance()).isEqualTo(남부터미널역_양재역_거리 + 강남역_양재역_거리);

    }
}
