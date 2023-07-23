package nextstep.subway.unit.line;

import nextstep.subway.common.exception.ValidationException;
import nextstep.subway.line.dto.ShortestPathResponse;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineRepository;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.entity.addition.SectionAdditionHandlerMapping;
import nextstep.subway.line.exception.StationNotFoundException;
import nextstep.subway.line.service.PathService;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.entity.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("경로 조회 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    Station 교대역 = new Station(1L, "교대역");
    Station 남부터미널역 = new Station(3L, "남부터미널역");
    Station 양재역 = new Station(4L, "양재역");
    Station 강남역 = new Station(2L, "강남역");
    Station 익명역 = new Station(5L, "익명역");

    Line 이호선;
    Line 삼호선;
    Line 신분당선;

    /**
     * 교대역 --- *2호선* --- 강남역
     * ㅣ                     ㅣ
     * *3호선*              *신분당선*
     * ㅣ                       ㅣ
     * 남부터미널역 --- *3호선* --- 양재역
     */

    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "green", 10, 교대역, 강남역);
        삼호선 = new Line("삼호선", "orange", 2, 교대역, 남부터미널역);
        신분당선 = new Line("신분당선", "red", 3, 강남역, 양재역);
        삼호선.addSection(new SectionAdditionHandlerMapping(), new Section(삼호선, 남부터미널역, 양재역, 3));
    }

    @Nested
    @DisplayName("최단 경로 길 찾기에 성공한다")
    class ShortestPathFindingSuccess {

        PathService pathService;

        @BeforeEach
        void setUp() {
            // given
            LineRepository lineRepository = mock(LineRepository.class);
            StationRepository stationRepository = mock(StationRepository.class);
            pathService = new PathService(lineRepository, stationRepository);
            List<Line> lineList = List.of(이호선, 삼호선, 신분당선);

            when(stationRepository.findById(1L)).thenReturn(Optional.of(교대역));
            when(stationRepository.findById(2L)).thenReturn(Optional.of(강남역));
            when(lineRepository.findAll()).thenReturn(lineList);
        }

        @DisplayName("A역에서 B역으로 갈 때")
        @Test
        void findShortestPath() {
            // when
            ShortestPathResponse response = pathService.getShortestPath(교대역.getId(), 강남역.getId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.getStations().stream()
                    .map(StationResponse::getId)
                    .collect(Collectors.toList()))
                    .containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId(), 강남역.getId());
            assertThat(response.getDistance()).isEqualTo(8);
        }

        @DisplayName("B역에서 A역으로 갈 때")
        @Test
        void findShortestPathReverse() {
            // when
            ShortestPathResponse response = pathService.getShortestPath(강남역.getId(), 교대역.getId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.getStations().stream()
                    .map(StationResponse::getId)
                    .collect(Collectors.toList()))
                    .containsExactly(강남역.getId(), 양재역.getId(), 남부터미널역.getId(), 교대역.getId());
            assertThat(response.getDistance()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("최단 경로 길 찾기에 실패한다.")
    class ShortestPathFindingFails {

        PathService pathService;
        LineRepository lineRepository;
        StationRepository stationRepository;
        List<Line> lineList;

        @BeforeEach
        void setUp() {
            // given
            lineRepository = mock(LineRepository.class);
            stationRepository = mock(StationRepository.class);
            pathService = new PathService(lineRepository, stationRepository);
            lineList = List.of(이호선, 삼호선, 신분당선);

        }

        @DisplayName("출발 역과 도착 역이 같은 경우")
        @Test
        void sourceAndTargetStationIsSame() {
            // when
            when(lineRepository.findAll()).thenReturn(lineList);
            when(stationRepository.findById(1L)).thenReturn(Optional.of(교대역));
            when(stationRepository.findById(1L)).thenReturn(Optional.of(교대역));

            // then
            Assertions.assertThatThrownBy(() -> pathService.getShortestPath(교대역.getId(), 교대역.getId()))
                    .isInstanceOf(ValidationException.class);
        }


        @DisplayName("출발 역과 도착 역이 연결되있지 않은 경우")
        @Test
        void sourceAndTargetStationIsNotConnected() {
            // when
            when(lineRepository.findAll()).thenReturn(lineList);
            when(stationRepository.findById(1L)).thenReturn(Optional.of(교대역));
            when(stationRepository.findById(5L)).thenReturn(Optional.of(익명역));

            // then
            Assertions.assertThatThrownBy(() -> pathService.getShortestPath(교대역.getId(), 익명역.getId()))
                    .isInstanceOf(ValidationException.class);
        }


        @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
        @Test
        void sourceOrTargetStationDoNotExist() {
            // when
            when(stationRepository.findById(1L)).thenReturn(Optional.of(교대역));
            when(stationRepository.findById(5L)).thenReturn(Optional.empty());

            // then
            Assertions.assertThatThrownBy(() -> pathService.getShortestPath(교대역.getId(), 익명역.getId()))
                    .isInstanceOf(StationNotFoundException.class);
        }
    }
}
