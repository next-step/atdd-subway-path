package nextstep.subway.path.application;

import nextstep.subway.exception.NoPathExistsException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.NotValidRequestException;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.application.MapService;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@DisplayName("경로 탐색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private MapService mapService;
    @Mock
    private StationRepository stationRepository;
    private PathService pathService;
    private LineResponse lineResponse1;
    private LineResponse lineResponse2;
    private LineResponse lineResponse3;
    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;


    @BeforeEach
    void setUp() {
        //given
        pathService = new PathService(mapService, stationRepository);
        station1 = reflectionStation(1L, "강남역");
        station2 = reflectionStation(2L, "역삼역");
        station3 = reflectionStation(3L, "선릉역");
        station4 = reflectionStation(4L, "양재역");
        station5 = reflectionStation(5L, "남부터미널역");

        LineStationResponse lineStation1 = new LineStationResponse(StationResponse.of(station1), null, 0, 0);
        LineStationResponse lineStation2 = new LineStationResponse(StationResponse.of(station2), 1L, 5, 5);
        LineStationResponse lineStation3 = new LineStationResponse(StationResponse.of(station3), 2L, 5, 5);
        LineStationResponse lineStation4 = new LineStationResponse(StationResponse.of(station3), null, 0, 0);
        LineStationResponse lineStation5 = new LineStationResponse(StationResponse.of(station4), 3L, 5, 5);
        LineStationResponse lineStation6 = new LineStationResponse(StationResponse.of(station5), null, 0, 0);

        lineResponse1 = new LineResponse(1L, "2호선", "GREEN",
                LocalTime.now(), LocalTime.now(), 5,
                Lists.list(lineStation1, lineStation2, lineStation3), LocalDateTime.now(), LocalDateTime.now());
        lineResponse2 = new LineResponse(2L, "신분당선", "RED",
                LocalTime.now(), LocalTime.now(), 5,
                Lists.list(lineStation4, lineStation5), LocalDateTime.now(), LocalDateTime.now());
        lineResponse3 = new LineResponse(3L, "3호선", "ORANGE",
                LocalTime.now(), LocalTime.now(), 5,
                Lists.list(lineStation6), LocalDateTime.now(), LocalDateTime.now());
    }

    private Station reflectionStation(long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        ReflectionTestUtils.setField(station, "createdDate", LocalDateTime.now());
        ReflectionTestUtils.setField(station, "modifiedDate", LocalDateTime.now());
        return station;
    }

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 같은 경우 에러가 발생한다.")
    @Test
    void findPathWithSameStartAndEndStation() {
        //when
        assertThatThrownBy(() -> pathService.findPath(1L, 1L, PathType.DISTANCE))
                //then
                .isInstanceOf(NotValidRequestException.class);
    }

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 연결이 되어 있지 않은 경우 에러가 발생한다.")
    @Test
    void findPathWithNotConnectedStations() {
        //given
        given(mapService.getMaps())
                .willReturn(MapResponse.of(Lists.list(lineResponse1, lineResponse2, lineResponse3)));

        //when
        assertThatThrownBy(() -> pathService.findPath(1L, 5L, PathType.DISTANCE))
                //then
                .isInstanceOf(NoPathExistsException.class);

    }

    @DisplayName("최단 경로 탐색 요청 시, 존재하지 않은 출발역이나 도착역을 조회 할 경우 에러가 발생한다.")
    @Test
    void findPathWithNotExistStations() {
        //given
        given(mapService.getMaps())
                .willReturn(MapResponse.of(Lists.list(lineResponse1, lineResponse2, lineResponse3)));
        //when
        assertThatThrownBy(() -> pathService.findPath(5L, 6L, PathType.DISTANCE))
                //then
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("최단 경로 탐색하여 최단경로를 리턴한다.")
    @Test
    void findPath() {
        //given
        given(mapService.getMaps())
                .willReturn(MapResponse.of(Lists.list(lineResponse1, lineResponse2, lineResponse3)));
        given(stationRepository.findAllById(anyList()))
                .willReturn(Lists.list(station1, station2, station3, station4));
        //when
        PathResponse shortestPath = pathService.findPath(1L, 4L, PathType.DISTANCE);

        //then
        assertThat(shortestPath.getStations()).hasSize(4)
                .extracting(StationResponse::getId)
                .containsExactly(1L, 2L, 3L, 4L);
        assertThat(shortestPath.getDistance()).isEqualTo(15);
        assertThat(shortestPath.getDuration()).isEqualTo(15);
    }

}