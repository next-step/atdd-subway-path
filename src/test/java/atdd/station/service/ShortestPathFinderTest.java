package atdd.station.service;

import atdd.line.domain.Line;
import atdd.line.domain.LineTest;
import atdd.line.domain.TimeTable;
import atdd.station.domain.Duration;
import atdd.station.domain.Station;
import atdd.station.domain.StationTest;
import atdd.station.dto.PathResponseDto;
import atdd.station.dto.PathStation;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ShortestPathFinderTest {

    private ShortestPathFinder shortestPathFinder;

    @BeforeEach
    void setup() {
        this.shortestPathFinder = new ShortestPathFinder(new WeightedMultigraphFactory());
    }

    @Test
    void findPath() throws Exception {
        final TimeTable timeTable = TimeTable.MAX_INTERVAL_TIME_TABLE;
        final Line line2 = LineTest.create(1L, "2호선", timeTable, 5);
        final Line line3 = LineTest.create(2L, "3호선", timeTable, 5);

        final Station samsung = StationTest.create(1L, "삼성역");
        final Station seonreung = StationTest.create(2L, "선릉역");
        final Station yeoksam = StationTest.create(3L, "역삼역");
        final Station gangnam = StationTest.create(4L, "강남역");
        final Station gyodae = StationTest.create(5L, "교대역");
        final Station terminal = StationTest.create(6L, "고속터미널역");

        LineTest.addStations(line2, samsung, seonreung, yeoksam, gangnam, gyodae);
        LineTest.addStations(line3, gyodae, terminal);


        final Duration duration = new Duration(LocalTime.of(0, 5));
        line2.addSection(samsung.getId(), seonreung.getId(), duration, 0.5);
        line2.addSection(seonreung.getId(), yeoksam.getId(), duration, 0.5);
        line2.addSection(yeoksam.getId(), gangnam.getId(), duration, 0.5);
        line2.addSection(gangnam.getId(), gyodae.getId(), duration, 0.5);
        line3.addSection(terminal.getId(), gyodae.getId(), duration, 0.5);

        final List<PathStation> expectedStations = Lists.list(
                PathStation.of(terminal.getId(), terminal.getName()),
                PathStation.of(gyodae.getId(), gyodae.getName()),
                PathStation.of(gangnam.getId(), gangnam.getName()),
                PathStation.of(yeoksam.getId(), yeoksam.getName()),
                PathStation.of(seonreung.getId(), seonreung.getName()),
                PathStation.of(samsung.getId(), samsung.getName())
        );


        final PathResponseDto result = shortestPathFinder.findPath(terminal, samsung);


        assertThat(result.getStartStationId()).isEqualTo(terminal.getId());
        assertThat(result.getEndStationId()).isEqualTo(samsung.getId());

        final List<PathStation> pathStations = result.getStations();
        assertThat(pathStations).hasSize(6);
        assertThat(pathStations).isEqualTo(expectedStations);
    }

    @DisplayName("findPath - 경로를 찾을 수 없으면 빈 배열을 반환한다.")
    @Test
    void findPathNotFound() throws Exception {
        final TimeTable timeTable = TimeTable.MAX_INTERVAL_TIME_TABLE;
        final Line line2 = LineTest.create(1L, "2호선", timeTable, 5);
        final Line line3 = LineTest.create(2L, "3호선", timeTable, 5);

        final Station samsung = StationTest.create(1L, "삼성역");
        final Station seonreung = StationTest.create(2L, "선릉역");
        final Station yeoksam = StationTest.create(3L, "역삼역");
        final Station gangnam = StationTest.create(4L, "강남역");
        final Station gyodae = StationTest.create(5L, "교대역");
        final Station terminal = StationTest.create(6L, "고속터미널역");

        LineTest.addStations(line2, samsung, seonreung, yeoksam, gangnam, gyodae);
        LineTest.addStations(line3, gyodae, terminal);


        final Duration duration = new Duration(LocalTime.of(0, 5));
        line2.addSection(samsung.getId(), seonreung.getId(), duration, 0.5);
        line2.addSection(seonreung.getId(), yeoksam.getId(), duration, 0.5);
        line2.addSection(yeoksam.getId(), gangnam.getId(), duration, 0.5);
        line3.addSection(terminal.getId(), gyodae.getId(), duration, 0.5);

        final PathResponseDto result = shortestPathFinder.findPath(terminal, samsung);


        assertThat(result.getStartStationId()).isEqualTo(terminal.getId());
        assertThat(result.getEndStationId()).isEqualTo(samsung.getId());

        final List<PathStation> pathStations = result.getStations();
        assertThat(pathStations).isEmpty();
    }

    @DisplayName("findPath - 출발역과 도착역이 동일하면 입력받은 역을 반환한다.")
    @Test
    void findPathWithSameStation() throws Exception {
        final TimeTable timeTable = TimeTable.MAX_INTERVAL_TIME_TABLE;
        final Line line2 = LineTest.create(1L, "2호선", timeTable, 5);
        final Station samsung = StationTest.create(1L, "삼성역");
        final Station seonreung = StationTest.create(2L, "선릉역");

        LineTest.addStations(line2, samsung, seonreung);


        final Duration duration = new Duration(LocalTime.of(0, 5));
        line2.addSection(samsung.getId(), seonreung.getId(), duration, 0.5);

        final PathStation expectedStation = PathStation.from(samsung);


        final PathResponseDto result = shortestPathFinder.findPath(samsung, samsung);


        assertThat(result.getStartStationId()).isEqualTo(samsung.getId());
        assertThat(result.getEndStationId()).isEqualTo(samsung.getId());

        final List<PathStation> pathStations = result.getStations();
        assertThat(pathStations).hasSize(1);
        assertThat(pathStations.get(0)).isEqualTo(expectedStation);
    }

}