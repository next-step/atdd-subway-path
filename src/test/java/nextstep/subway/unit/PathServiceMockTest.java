package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BadRequestPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nextstep.subway.acceptance.steps.SectionSteps.지하철_노선_구간_등록;
import static nextstep.subway.unit.fixture.LineFixture.지하철_노선_생성;
import static nextstep.subway.unit.fixture.SectionFixture.지하철_구간_생성;
import static nextstep.subway.unit.fixture.StationFixture.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DisplayName("경로 조회 서비스 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    private StationService stationService;
    @Mock
    private LineRepository lineRepository;
    @InjectMocks
    private PathService pathService;

    private static final int DEFAULT_DISTANCE = 10;
    private static final int SHORT_DISTANCE = 5;

    private Line 신분당선, 이호선, 삼호선;
    private Station 교대역, 강남역, 양재역, 남부터미널역;

    private List<Section> sections;

    @BeforeEach
    void set() {
        교대역 = 지하철역_생성("교대역");
        강남역 = 지하철역_생성("강남역");
        양재역 = 지하철역_생성("양재역");
        남부터미널역 = 지하철역_생성("남부터미널역");

        신분당선 = 지하철_노선_생성("신분당선", "bg-red-600", 강남역, 양재역, DEFAULT_DISTANCE);
        이호선 = 지하철_노선_생성("2호선", "bg-red-600", 강남역, 교대역, DEFAULT_DISTANCE);
        삼호선 = 지하철_노선_생성("3호선", "bg-red-600", 교대역, 남부터미널역, DEFAULT_DISTANCE);

        삼호선.addSections(지하철_구간_생성(삼호선, 남부터미널역, 양재역, SHORT_DISTANCE));

        sections = new ArrayList<>();
        sections.addAll(신분당선.getSections());
        sections.addAll(이호선.getSections());
        sections.addAll(삼호선.getSections());
    }

    @DisplayName("경로 조회")
    @Test
    void findShortestPath() {
        given(lineRepository.findAll()).willReturn(Arrays.asList(신분당선, 이호선, 삼호선));
        given(stationService.getStations(교대역.getId())).willReturn(교대역);
        given(stationService.getStations(양재역.getId())).willReturn(양재역);

        PathResponse paths = pathService.getPaths(교대역.getId(), 양재역.getId());

        assertThat(paths.getStations().stream().mapToLong(it->it.getId())).containsExactly(교대역.getId(),남부터미널역.getId(),양재역.getId());
        assertThat(paths.getStations().size()).isEqualTo(3);
    }

    @DisplayName("출발역과 도착역이 동일한 경로를 조회할 경우 에러를 던짐")
    @Test
    void findSameSourceAndTargetPath() {
        assertThrows(BadRequestPathException.class, () -> pathService.getPaths(양재역.getId(), 양재역.getId()));
    }

    @DisplayName("연결되지 않은 경로를 조회할 경우 에러를 던짐")
    @Test
    void findUnconnectedPath() {
        Station 대방역 = 지하철역_생성("대방역");
        Station 신림역 = 지하철역_생성("신림역");
        Line 신림선 = 지하철_노선_생성("신림선", "bg-red-600", 대방역, 신림역, DEFAULT_DISTANCE);

        given(lineRepository.findAll()).willReturn(Arrays.asList(신분당선, 이호선, 삼호선, 신림선));
        given(stationService.getStations(대방역.getId())).willReturn(대방역);
        given(stationService.getStations(양재역.getId())).willReturn(양재역);

        assertThrows(BadRequestPathException.class, () -> pathService.getPaths(대방역.getId(), 양재역.getId()));
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회할 경우 에러를 던짐")
    @Test
    void findNoExistedStationPath() {
        Station 존재하지_않는_역 = new Station("테스트");

        given(lineRepository.findAll()).willReturn(Arrays.asList(신분당선, 이호선, 삼호선));
        given(stationService.getStations(존재하지_않는_역.getId())).willReturn(null);
        given(stationService.getStations(양재역.getId())).willReturn(양재역);

        assertThrows(IllegalArgumentException.class, () -> pathService.getPaths(존재하지_않는_역.getId(), 양재역.getId()));
    }
}
