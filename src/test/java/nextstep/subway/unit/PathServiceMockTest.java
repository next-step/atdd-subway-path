package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 컨트롤러 구현 이후 서비스 구현 시 바로 기능 구현에 앞서 단위 테스트 먼저 작성
 * 서비스 레이어의 단위 테스트 목적은 비즈니스 플로우를 검증하는 것이며 이 때 협력 객체는 stubbing을 활용하여 대체함
 *
 * 단위 테스트 작성 후 해당 단위 테스트를 만족하는 기능을 구현한 다음 리팩터링 진행
 * 그 다음 기능 구현은 방금 전 사이클에서 stubbing 한 객체를 대상으로 진행하면 수월하게 TDD 사이클을 진행할 수 있음
 *
 * 외부 라이브러리를 활용한 로직을 검증할 때는 가급적 실제 객체를 활용
 * Happy 케이스에 대한 부분만 구현 (Side 케이스에 대한 구현은 다음 단계에서 진행)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("경로 조회 단위 테스트 - stub")
@Transactional
public class PathServiceMockTest {

    @InjectMocks
    private PathService pathService;
    @Mock
    private StationService stationService = mock(StationService.class);
    @Mock
    private LineService lineService = mock(LineService.class);

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    /**
     * 교대역 --- 거리: 10, *2호선* --- 강남역
     * |                              |
     * 거리: 2, *3호선*               거리: 10, *신분당선*
     * |                                 |
     * 남부터미널역 --- 거리: 3, *3호선* --- 양재역
     */
    @BeforeEach
    public void setUp() {
        교대역 = SavingStub.createStation("교대역");
        강남역 = SavingStub.createStation("강남역");
        양재역 = SavingStub.createStation("양재역");
        남부터미널역 = SavingStub.createStation("남부터미널역");

        이호선 = SavingStub.createLine("2호선", "green");
        이호선.addSection(SavingStub.createSection(이호선, 교대역, 강남역, 10));

        신분당선 = SavingStub.createLine("신분당선", "red");
        신분당선.addSection(SavingStub.createSection(신분당선, 강남역, 양재역, 10));

        삼호선 = SavingStub.createLine("3호선", "orange");
        삼호선.addSection(SavingStub.createSection(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(SavingStub.createSection(삼호선, 남부터미널역, 양재역, 3));
    }

    private static class SavingStub {
        private static Station createStation(String name) {
            Station station = new Station(name);
            ReflectionTestUtils.setField(station, "id", idGenerator.generate());
            return station;
        }

        private static Line createLine(String name, String color) {
            Line line = new Line(name, color);
            ReflectionTestUtils.setField(line, "id", idGenerator.generate());
            return line;
        }

        private static Section createSection(Line line, Station upStation, Station downStation, int distance) {
            Section section = new Section(line, upStation, downStation, distance);
            ReflectionTestUtils.setField(section, "id", idGenerator.generate());
            return section;
        }

        private static class idGenerator {
            private static Long id = 0L;

            private static Long generate() {
                return id++;
            }
        }
    }

    @DisplayName("출발역과 도착역의 최단거리를 조회한다")
    @Test
    void findShortestPath() {
        // Given
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(남부터미널역.getId())).thenReturn(남부터미널역);
        when(lineService.findAll()).thenReturn(List.of(이호선, 신분당선, 삼호선));

        // When
        PathResponse response = pathService.findShortestPath(강남역.getId(), 남부터미널역.getId());

        // Then
        assertThat(response.getStations()).hasSize(3);
        assertThat(response.getDistance()).isEqualTo(12);
    }


}
