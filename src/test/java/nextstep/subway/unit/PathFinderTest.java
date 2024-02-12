package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PathFinderTest {

        private PathFinder pathFinder;

        private PathRequest pathRequest;
        private List<Line> lines;

        @BeforeEach
        public void setUp() {
            final Station station1 = new Station(1L, "강남역");
            final Station station2 = new Station(2L, "역삼역");
            final Line line = new Line("2호선", "green", station1, station2, 10);
            lines = List.of(line);

            pathRequest = new PathRequest(1L, 2L);
            pathFinder = new PathFinderMock();
        }

        @Test
        public void 경로_조회_기능() {
            assertThat(pathFinder.findPath(pathRequest, lines)).hasOnlyFields("distance", "stations");
        }
}

class PathFinderMock extends PathFinder {
    @Override
    protected PathResponse getPath(PathRequest pathRequest, List<Line> lines) {
        return new PathResponse();
    }
}
