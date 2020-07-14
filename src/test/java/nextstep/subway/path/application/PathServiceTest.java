package nextstep.subway.path.application;

import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("경로 탐색 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 같은 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithSameStartAndEndStation() {
    }

    @DisplayName("최단 경로 탐색 요청 시, 출발역과 도착역이 연결이 되어 있지 않은 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithNotConnectedStations() {
    }

    @DisplayName("최단 경로 탐색 요청 시, 존재하지 않은 출발역이나 도착역을 조회 할 경우 에러가 발생한다.")
    @Test
    void findShortestPathWithNotExistStations() {
    }

    @DisplayName("최단 경로 탐색하여 최단경로를 리턴한다.")
    @Test
    void findShortestPath() {
    }

    @DisplayName("최단 경로 탐색하여 여러개의 최단 경로가 나와도 하나의 최단 경로만 리턴한다.")
    @Test
    void findShortestPathWithMultipleAnswers() {
    }
}