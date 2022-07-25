package nextstep.subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;

@DataJpaTest
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void temp() {
        final Line line1 = new Line();
        final Line line2 = new Line();
        final Line line3 = new Line();

        final Station station1 = stationRepository.save(station(1));
        final Station station2 = stationRepository.save(station(2));
        final Station station3 = stationRepository.save(station(3));
        final Station station4 = stationRepository.save(station(4));
        final Station station5 = stationRepository.save(station(5));
        final Station station6 = stationRepository.save(station(6));
        final Station station7 = stationRepository.save(station(7));
        final Station station8 = stationRepository.save(station(8));
        final Station station9 = stationRepository.save(station(9));
        final Station station10 = stationRepository.save(station(10));
        final Station station11 = stationRepository.save(station(11));
        final Station station12 = stationRepository.save(station(12));
        final Station station13 = stationRepository.save(station(13));
        final Station station14 = stationRepository.save(station(14));
        final Station station15 = stationRepository.save(station(15));

        line1.addSection(section(station1, station2));
        line1.addSection(section(station2, station3));
        line1.addSection(section(station3, station4));
        line2.addSection(section(station4, station5));
        line2.addSection(section(station5, station6));
        line2.addSection(section(station6, station7));
        line2.addSection(section(station7, station8));
        line2.addSection(section(station8, station9));
        line3.addSection(section(station9, station10));
        line3.addSection(section(station10, station11));
        line3.addSection(section(station11, station12));
        line3.addSection(section(station12, station13));
        line3.addSection(section(station13, station14));
        line3.addSection(section(station14, station15));

        final Line savedLine1 = lineRepository.save(line1);
        final Line savedLine2 = lineRepository.save(line2);
        final Line savedLine3 = lineRepository.save(line3);

        entityManager.clear();

        for (Line v : lineRepository.findAll()) {
            v.getSections().stream().mapToInt(a -> a.getDistance()).sum();
        }
        final Line result1 = lineRepository.findById(savedLine1.getId())
                .orElseThrow();

        final Line result2 = lineRepository.findById(savedLine2.getId())
                .orElseThrow();

        final Line result3 = lineRepository.findById(savedLine3.getId())
                .orElseThrow();

        result1.getSections().forEach(System.out::println);
        result2.getSections().forEach(System.out::println);
        result3.getSections().forEach(System.out::println);
    }

}