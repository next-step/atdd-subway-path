package nextstep.subway.unit.service;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nextstep.subway.unit.PathUnitTestHelper.STATIONS_SIZE_YANGJAE_TO_YEOKSAM;
import static nextstep.subway.unit.PathUnitTestHelper.createPathRequest;
import static nextstep.subway.unit.PathUnitTestHelper.givens;
import static nextstep.subway.unit.PathUnitTestHelper.lines;
import static nextstep.subway.unit.PathUnitTestHelper.양재역;
import static nextstep.subway.unit.PathUnitTestHelper.역삼역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private PathService pathService;

    private PathRequest pathRequest;

    @BeforeEach
    void setFixtures() {
        givens();
        pathRequest = createPathRequest();
    }

    @Test
    void 최단_경로_조회() {
        when(lineRepository.findAll()).thenReturn(lines);
        when(stationService.findStationById(양재역.getId())).thenReturn(양재역);
        when(stationService.findStationById(역삼역.getId())).thenReturn(역삼역);
        PathResponse response = pathService.findShortestPath(pathRequest);
        assertThat(response.getStations()).hasSize(STATIONS_SIZE_YANGJAE_TO_YEOKSAM);
    }
}
