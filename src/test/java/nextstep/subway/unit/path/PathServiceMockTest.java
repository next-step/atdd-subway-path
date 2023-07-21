package nextstep.subway.unit.path;


import nextstep.subway.line.algorithm.ShortestPathFinder;
import nextstep.subway.line.dto.ShortestPathResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.LineRepository;
import nextstep.subway.line.service.PathService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@DisplayName("경로 조회 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {


    /**
     * 교대역 --- *2호선* --- 강남역
     * ㅣ                     ㅣ
     * *3호선*              *신분당선*
     * ㅣ                       ㅣ
     * 남부터미널역 --- *3호선* --- 양재역
     * *
     * <p>
     * /**
     * Given
     * When 교대에서 강남역으로 가는 최단 경로를 조회하면
     * Then 모든 노선 정보를 최단 경로 찾기 객체에 넣는다
     * And 최단 경로 찾기 객체의 getPath를 호출한다.
     */
    @DisplayName("최단 경로 길 찾기")
    @Test
    void findShortestPath() {
        LineRepository lineRepository = mock(LineRepository.class);
        PathService pathService = new PathService(lineRepository);


        // given
        List<Line> lineList = new ArrayList<>();
        when(lineRepository.findAll()).thenReturn(lineList);
        ShortestPathFinder shortestPathFinder = mock(ShortestPathFinder.class);

        // when
        ShortestPathResponse response = pathService.getShortestPath(1, 2);

        // then
        verify(shortestPathFinder).getPath(1, 2);
        verify(shortestPathFinder).getWeight();
    }
}
