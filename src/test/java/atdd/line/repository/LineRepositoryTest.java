package atdd.line.repository;

import atdd.line.domain.Line;
import atdd.line.domain.LineStation;
import atdd.line.domain.TimeTable;
import atdd.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineStationRepository lineStationRepository;

    @Test
    void save() throws Exception {
        final String name = "name!!";
        final TimeTable timeTable = new TimeTable(LocalTime.MIN, LocalTime.MIDNIGHT);
        final int interval = 6;
        final Line line = Line.create(name, timeTable, interval);

        final Line save = lineRepository.save(line);

        final Line find = lineRepository.findById(save.getId()).orElseThrow(EntityNotFoundException::new);

        assertThat(find.getId()).isNotNull();
        assertThat(find.getName()).isEqualTo(name);
        assertThat(find.getTimeTable()).isEqualTo(timeTable);
    }

    @DisplayName("save - 동일한 이름 저장 불가")
    @Test
    void saveSameName() throws Exception {
        final String name = "name!!";
        final TimeTable timeTable = new TimeTable(LocalTime.MIN, LocalTime.MIDNIGHT);
        final int interval = 6;

        lineRepository.save(Line.create(name, timeTable, interval));
        lineRepository.flush();


        assertThatThrownBy(() -> lineRepository.save(Line.create(name, timeTable, interval)))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("새로운 station 추가")
    @Test
    void addStation() throws Exception {
        final Station station = Station.create("stationName!!");
        Line line = getSavedLine();


        line.addStation(station);
        lineRepository.flush();


        final List<Station> stations = line.getStations();
        assertThat(stations).hasSize(1);

        final Station addedStation = stations.get(0);
        assertThat(addedStation.getId()).isNotNull();
        assertThat(addedStation.getName()).isEqualTo(station.getName());

        assertThat(line.getStartStation()).isEqualTo(addedStation);
    }

    @Test
    void delete() throws Exception {
        final Station station = Station.create("stationName!!");
        Line line = getSavedLine();
        line.addStation(station);
        lineRepository.flush();

        lineRepository.delete(line);
        lineRepository.flush();

        final List<LineStation> lineStations = lineStationRepository.findAll();
        assertThat(lineStations).isEmpty();
    }

    private Line getSavedLine() {
        final String name = "name!!";
        final TimeTable timeTable = new TimeTable(LocalTime.MIN, LocalTime.MIDNIGHT);
        final int interval = 6;
        return lineRepository.save(Line.create(name, timeTable, interval));
    }

}