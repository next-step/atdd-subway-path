package nextstep.subway.unit.path;

import nextstep.subway.fixture.StationFixture;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidPathException;
import nextstep.subway.global.error.exception.NotEntityFoundException;
import nextstep.subway.line.adapters.persistence.LineJpaAdapter;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.dto.request.PathRequest;
import nextstep.subway.path.dto.response.PathResponse;
import nextstep.subway.path.service.PathService;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.adapters.persistence.StationJpaAdapter;
import nextstep.subway.station.dto.response.StationResponse;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    private PathService pathService;

    private static final Long 까치산역_아이디 = StationFixture.까치산역.getId();

    private static final Long 신도림역_아이디 = StationFixture.신도림역.getId();

    private static final Long 신촌역_아이디 = StationFixture.신촌역.getId();

    private static final Long 잠실역_아이디 = StationFixture.잠실역.getId();

    private Line 이호선;

    /**
     * <pre>
     *            신촌역    -------------------   *2호선(19)*
     *            |                               |
     *            *2호선(6)*                      |
     *            |                              |
     * 까치산역    신도림역  --- *2호선(18)* --- 잠실역
     * </pre>
     */
    @BeforeEach
    void setUp() {
        LineJpaAdapter lineJpaAdapter = new LineJpaAdapter(lineRepository);
        StationJpaAdapter stationJpaAdapter = new StationJpaAdapter(stationRepository);
        pathService = new PathService(stationJpaAdapter, lineJpaAdapter);

        // given
        이호선 = Line.builder()
                .name("2호선")
                .color("#52c41a")
                .upStation(StationFixture.신도림역)
                .downStation(StationFixture.신촌역)
                .distance(6)
                .build();

        Section 신촌역_잠실역_구간 = Section.builder()
                .upStation(StationFixture.신촌역)
                .downStation(StationFixture.잠실역)
                .distance(19)
                .build();
        Section 잠실역_신도림역_구간 = Section.builder()
                .upStation(StationFixture.잠실역)
                .downStation(StationFixture.신도림역)
                .distance(18)
                .build();

        Stream.of(신촌역_잠실역_구간, 잠실역_신도림역_구간).forEach(이호선::addSection);
    }

    @Test
    @DisplayName("최단 거리 경로를 조회한다.")
    void getShortestDistance() {
        // given
        given(stationRepository.findById(신도림역_아이디)).willReturn(Optional.of(StationFixture.신도림역));
        given(stationRepository.findById(잠실역_아이디)).willReturn(Optional.of(StationFixture.잠실역));
        given(stationRepository.findAll()).willReturn(
                List.of(StationFixture.까치산역, StationFixture.신도림역, StationFixture.신촌역, StationFixture.잠실역)
        );
        given(lineRepository.findAll()).willReturn(List.of(이호선));

        PathRequest 출발역_신도림역_도착역_잠실역_요청 = PathRequest.builder()
                .source(신도림역_아이디)
                .target(잠실역_아이디)
                .build();

        // when
        PathResponse 최단_거리_경로_조회_응답 = pathService.getShortestDistance(출발역_신도림역_도착역_잠실역_요청);

        // then
        List<Long> 최단_거리_경로에_존재하는_역_아이디_목록 = 최단_거리_경로_조회_응답.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        Integer 최단_거리 = 최단_거리_경로_조회_응답.getDistance();

        assertAll(
                () -> assertThat(최단_거리_경로에_존재하는_역_아이디_목록).containsExactly(신도림역_아이디, 잠실역_아이디),
                () -> assertThat(최단_거리).isEqualTo(18)
        );
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경로를 조회한다.")
    void getSameDepartureAndArrivalStations() {
        // given
        PathRequest 출발역_신촌역_도착역_신촌역_요청 = PathRequest.builder()
                .source(신촌역_아이디)
                .target(신촌역_아이디)
                .build();

        // when & then
        assertThatThrownBy(() -> pathService.getShortestDistance(출발역_신촌역_도착역_신촌역_요청))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining(ErrorCode.SAME_DEPARTURE_AND_ARRIVAL_STATIONS.getMessage());
    }

    @Test
    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우에 경로를 조회한다.")
    void getUnlinkedDepartureAndArrivalStations() {
        // given
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(stationRepository.findById(까치산역_아이디)).willReturn(Optional.of(StationFixture.까치산역));
        given(stationRepository.findAll()).willReturn(
                List.of(StationFixture.까치산역, StationFixture.신도림역, StationFixture.신촌역, StationFixture.잠실역)
        );
        given(lineRepository.findAll()).willReturn(List.of(이호선));

        PathRequest 출발역_신촌역_도착역_까치산역_요청 = PathRequest.builder()
                .source(신촌역_아이디)
                .target(까치산역_아이디)
                .build();

        // when & then
        assertThatThrownBy(() -> pathService.getShortestDistance(출발역_신촌역_도착역_까치산역_요청))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining(ErrorCode.UNLINKED_DEPARTURE_AND_ARRIVAL_STATIONS.getMessage());
    }

    @Test
    @DisplayName("등록되어 있지 않은 역이 출발역인 최단 경로를 조회한다.")
    void getShortestDistanceWhenNotExistDepartureStation() {
        // given
        given(stationRepository.findById(신도림역_아이디)).willReturn(Optional.empty());

        PathRequest 출발역_신도림역_도착역_잠실역_요청 = PathRequest.builder()
                .source(신도림역_아이디)
                .target(잠실역_아이디)
                .build();

        // when & then
        assertThatThrownBy(() -> pathService.getShortestDistance(출발역_신도림역_도착역_잠실역_요청))
                .isInstanceOf(NotEntityFoundException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_STATION.getMessage());
    }

    @Test
    @DisplayName("등록되어 있지 않은 역이 도착역인 최단 경로를 조회한다.")
    void getShortestDistanceWhenNotExistArrivalStation() {
        // given
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(stationRepository.findById(신도림역_아이디)).willReturn(Optional.empty());

        PathRequest 출발역_신촌역_도착역_신도림역_요청 = PathRequest.builder()
                .source(신촌역_아이디)
                .target(신도림역_아이디)
                .build();

        // when & then
        assertThatThrownBy(() -> pathService.getShortestDistance(출발역_신촌역_도착역_신도림역_요청))
                .isInstanceOf(NotEntityFoundException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_STATION.getMessage());
    }
}
