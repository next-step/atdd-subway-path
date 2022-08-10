package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.utils.TestObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    StationService stationService;
    PathService pathService;

    @BeforeEach
    public void setUp() {
        stationService = new StationService(stationRepository);
        pathService = new PathService(lineRepository, stationService);
    }

    TestObjectFactory testObjectFactory = new TestObjectFactory();

    @Test
    @DisplayName("경로 조회")
    void getPath() {
        Line 분당선 = testObjectFactory.노선생성("분당선");
        Station 강남역 = testObjectFactory.역생성("강남역");
        Station 양재역 = testObjectFactory.역생성("양재역");
        Station 신논현역 = testObjectFactory.역생성("신논현역");

        Line 구호선 = testObjectFactory.노선생성("구호선");
        Station 고속터미널 = testObjectFactory.역생성("고속터미널");

        Section 강남_양재 = testObjectFactory.구간생성(분당선, 강남역, 양재역, 1000);
        Section 신논현_강남 = testObjectFactory.구간생성(분당선, 신논현역, 강남역, 10);
        Section 고터_신논현 = testObjectFactory.구간생성(구호선, 고속터미널, 신논현역, 10);

        분당선.addSection(강남_양재);
        분당선.addSection(신논현_강남);
        구호선.addSection(고터_신논현);

        when(lineRepository.findAll()).thenReturn(List.of(분당선, 구호선));
        when(stationRepository.findById(양재역.getId())).thenReturn(Optional.of(양재역));
        when(stationRepository.findById(고속터미널.getId())).thenReturn(Optional.of(고속터미널));

        PathResponse pathResponse = pathService.getPath(양재역.getId(), 고속터미널.getId());

        지하철_역이_나열된다(pathResponse, 양재역.getId(), 강남역.getId(), 신논현역.getId(), 고속터미널.getId());
        총거리를_확인한다(pathResponse, 강남_양재.getDistance(), 신논현_강남.getDistance(), 고터_신논현.getDistance());
    }

    private void 총거리를_확인한다(PathResponse pathResponse, Integer... distances) {
        assertThat(pathResponse.getDistance()).isEqualTo(Arrays.stream(distances).reduce(0, Integer::sum));
    }

    private void 지하철_역이_나열된다(PathResponse pathResponse, Long... stations) {
        List<Long> targetIds = pathResponse.getStations().stream().map(StationResponse::getId).collect(Collectors.toList());
        assertThat(targetIds).containsExactly(stations);
    }
}
