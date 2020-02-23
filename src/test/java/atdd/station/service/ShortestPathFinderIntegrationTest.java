package atdd.station.service;

import atdd.line.domain.Line;
import atdd.line.domain.LineTest;
import atdd.line.domain.TimeTable;
import atdd.line.repository.LineRepository;
import atdd.station.domain.Duration;
import atdd.station.domain.Station;
import atdd.station.dto.PathResponseDto;
import atdd.station.dto.PathStation;
import atdd.station.repository.StationRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ShortestPathFinderIntegrationTest {

    private ShortestPathFinder shortestPathFinder;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private WeightedMultigraphFactory weightedMultigraphFactory;

    @BeforeEach
    void setup() {
        this.shortestPathFinder = new ShortestPathFinder(weightedMultigraphFactory);
    }

    @Test
    void findPath() throws Exception {
        final Line line2 = saveLine("2호선");
        final Line line3 = saveLine("3호선");

        final Station samsung = saveStation("삼성역");
        final Station seonreung = saveStation("선릉역");
        final Station yeoksam = saveStation("역삼역");
        final Station gangnam = saveStation("강남역");
        final Station gyodae = saveStation("교대역");
        final Station terminal = saveStation("고속터미널역");

        LineTest.addStations(line2, samsung, seonreung, yeoksam, gangnam, gyodae);
        entityManager.flush();
        LineTest.addStations(line3, gyodae, terminal);
        entityManager.flush();


        final Duration duration = new Duration(LocalTime.of(0, 5));
        line2.addSection(samsung.getId(), seonreung.getId(), duration, 0.5);
        line2.addSection(seonreung.getId(), yeoksam.getId(), duration, 0.5);
        line2.addSection(yeoksam.getId(), gangnam.getId(), duration, 0.5);
        line2.addSection(gangnam.getId(), gyodae.getId(), duration, 0.5);
        line3.addSection(terminal.getId(), gyodae.getId(), duration, 0.5);
        entityManager.flush();

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

    private Line saveLine(String name) {
        final Line line = Line.create(name, TimeTable.MAX_INTERVAL_TIME_TABLE, 5);
        return lineRepository.save(line);
    }

    private Station saveStation(String name) {
        final Station station = Station.create(name);
        return stationRepository.save(station);
    }

}