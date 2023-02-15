package nextstep.subway.unit;

import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PathFinderTest {
    Station 교대역 = new Station(1L,"교대역");
    Station 강남역 = new Station(2L,"강남역");
    Station 양재역 = new Station(3L,"양재역");
    Station 남부터미널역 = new Station(4L, "남부터미널역");
    Line 이호선 = new Line(1L,"이호선", "yellow");
    Line 삼호선 = new Line(2L,"삼호선", "blue");
    Line 신분당선 = new Line(3L,"신분당선", "red");
    int distance_10 = 10;
    int distance_3 = 3;
    int distance_2 = 2;

    @Autowired
    PathFinder pathFinder;

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    @BeforeEach
    void setUp(){
        이호선 = 지하철노선_기존구간_추가(이호선, 교대역, 강남역, distance_10);
        신분당선 = 지하철노선_기존구간_추가(신분당선, 강남역, 양재역, distance_10);
        삼호선 = 지하철노선_기존구간_추가(삼호선, 교대역, 남부터미널역, distance_2);
        삼호선 = 지하철노선_기존구간_추가(삼호선, 양재역, 남부터미널역, distance_3);
    }

    @Test
    void searchShortPath(){
        List<Line> lines = new ArrayList<>();
        lines.add(이호선);
        lines.add(삼호선);
        lines.add(신분당선);

        pathFinder.initGraph(lines);

        Path path = pathFinder.searchShortPath(교대역.getId(), 양재역.getId());

        assertThat(path.getStationIds()).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(path.getDistance()).isEqualTo(5);
    }

    private Line 지하철노선_기존구간_추가(Line line, Station upStation, Station downStation, int distance) {
        Section section = createSection(line, upStation, downStation, distance);
        line.addSection(section);

        return line;
    }

    private Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(distance))
                .build();
    }
}
