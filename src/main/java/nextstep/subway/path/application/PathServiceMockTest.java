package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @Mock
    private LineService lineService;

    private PathService pathService;

    private Station 방배역;
    private Station 서초역;
    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 양재역;
    private Station 양재시민의숲역;
    private Station 청계산입구역;
    private Station 판교역;
    private Station 광교역;

    private Line 이호선;
    private Line 신분당선;

    private final static int DEFAULT_SECTION_DISTANCE = 10;

    @BeforeEach
    void setUp() {
        pathService = new PathService(lineService, stationService);

        역_생성();
        이호선_생성();
        신분당선_생성();
    }

    void 역_생성() {
        방배역 = new Station("방배역");
        ReflectionTestUtils.setField(방배역, "id", 1L);

        서초역 = new Station("서초역");
        ReflectionTestUtils.setField(서초역, "id", 2L);

        교대역 = new Station("교대역");
        ReflectionTestUtils.setField(교대역, "id", 3L);

        강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 4L);

        역삼역 = new Station("역삼역");
        ReflectionTestUtils.setField(역삼역, "id", 5L);

        양재역 = new Station("양재역");
        ReflectionTestUtils.setField(양재역, "id", 6L);

        양재시민의숲역 = new Station("양재시민의숲역");
        ReflectionTestUtils.setField(양재시민의숲역, "id", 7L);

        청계산입구역 = new Station("청계산입구역");
        ReflectionTestUtils.setField(청계산입구역, "id", 8L);

        판교역 = new Station("판교역");
        ReflectionTestUtils.setField(판교역, "id", 9L);

        광교역 = new Station("광교역");
        ReflectionTestUtils.setField(광교역, "id", 10L);
    }

    void 이호선_생성() {
        이호선 = new Line("2호선", "green", 방배역, 서초역, DEFAULT_SECTION_DISTANCE);
        ReflectionTestUtils.setField(이호선, "id", 1L);
    }

    void 신분당선_생성() {
        신분당선 = new Line("신분당선", "red", 강남역, 양재역, DEFAULT_SECTION_DISTANCE);
        ReflectionTestUtils.setField(신분당선, "id", 2L);
    }
}
