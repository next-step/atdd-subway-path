package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.path.PathResponse;
import nextstep.subway.applicaion.dto.station.StationResponse;
import nextstep.subway.applicaion.path.PathFinder;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.section.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.exception.SubwayRestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    List<Line> lines = new ArrayList<>();

    @Autowired
    StationRepository stationRepository;

    PathFinder pathFinder;

    @BeforeEach
    void setUp(){
        이호선 = 지하철노선_기존구간_추가(이호선, 교대역, 강남역, distance_10);
        신분당선 = 지하철노선_기존구간_추가(신분당선, 강남역, 양재역, distance_10);
        삼호선 = 지하철노선_기존구간_추가(삼호선, 교대역, 남부터미널역, distance_2);
        삼호선 = 지하철노선_기존구간_추가(삼호선, 양재역, 남부터미널역, distance_3);

        lines.add(이호선);
        lines.add(삼호선);
        lines.add(신분당선);

//        pathFinder = new PathFinder();
    }

    @Test
    void searchShortPath(){
        PathResponse path = pathFinder.findPath(lines, 교대역.getId(), 양재역.getId());

        assertThat(path.getStations().stream().map(StationResponse::getId).collect(Collectors.toList())).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(path.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역이랑 도착역이 같은 경우 Exception 던짐")
    @Test
    void searchShortPathException(){

        assertThrows(SubwayRestApiException.class, () -> pathFinder.findPath(lines, 교대역.getId(), 교대역.getId()));
    }

    @DisplayName("노선에 출발역 또는 도착역이 없는 경우 Exception 던짐")
    @Test
    void searchShortPathException2(){
        Station 없는지하철역 = new Station(5L, "없는지하철역");

        assertAll(() -> assertThrows(SubwayRestApiException.class, () -> pathFinder.findPath(lines, 교대역.getId(), 없는지하철역.getId())),
                () -> assertThrows(SubwayRestApiException.class, () -> pathFinder.findPath(lines, 없는지하철역.getId(), 교대역.getId())));
    }

    @DisplayName("출발역과 도착역이 연결되어있지 않는 경우 Exception 던짐")
    @Test
    void searchShortPathException3(){
        Station 오이도역 = new Station(5L,"오이도역");
        Station 사당역 = new Station(6L, "사당역");

        Line 사호선 = 지하철노선_기존구간_추가(new Line(4L,"사호선", "black"), 오이도역, 사당역, distance_3);
        lines.add(사호선);

        assertThrows(SubwayRestApiException.class, () -> pathFinder.findPath(lines, 교대역.getId(), 사당역.getId()));
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
