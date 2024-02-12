package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PathFinderTest {

        private PathFinder pathFinder;

        private PathRequest pathRequest;
        private List<Line> lines;

        @BeforeEach
        public void setUp() {
            pathRequest = new PathRequest(1L, 2L);
            lines = new ArrayList<>();
            pathFinder = new PathFinderMock();
        }

        @Test
        public void 경로_조회_기능() {
            PathResponse response = pathFinder.findPath(pathRequest, lines);

            assertThat(response).hasOnlyFields("distance", "stations");
        }

        @Test
        public void 출발지와_도착지가_같으면_익셉션을_던진다() {
            pathFinder.validateRequest(pathRequest, lines);
        }

}

class PathFinderMock implements PathFinder {

    @Override
    public PathResponse findPath(PathRequest pathRequest, List<Line> lines) {
        return new PathResponse();
    }

    @Override
    public void validateRequest(PathRequest request, List<Line> lines) {
        if (request.getSource().equals(request.getTarget())) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
    }
}
