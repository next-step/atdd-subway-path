package nextstep.subway.config;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataLoaderConfig implements CommandLineRunner {
    private StationRepository stationRepository;
    private LineRepository lineRepository;

    public DataLoaderConfig(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Station station1 = new Station("강남역");
        Station station2 = new Station("교대역");
        Station station3 = new Station("양재역");
        Station station4 = new Station("남부터미널역");
        stationRepository.saveAll(Lists.newArrayList(station1, station2, station3, station4));

        Line line1 = new Line("신분당선", "red lighten-1", LocalTime.now(), LocalTime.now(), 10);
        Line line2 = new Line("2호선", "green lighten-1", LocalTime.now(), LocalTime.now(), 10);
        Line line3 = new Line("3호선", "orange darken-1", LocalTime.now(), LocalTime.now(), 10);

        line1.registerLineStation(new LineStation(station1, null, 0, 0));
        line1.registerLineStation(new LineStation(station3, station1, 3, 1));

        line2.registerLineStation(new LineStation(station2, null, 0, 0));
        line2.registerLineStation(new LineStation(station1, station2, 3, 1));

        line3.registerLineStation(new LineStation(station2, null, 0, 0));
        line3.registerLineStation(new LineStation(station4, station2, 2, 10));
        line3.registerLineStation(new LineStation(station3, station4, 2, 10));

        lineRepository.saveAll(Lists.newArrayList(line1, line2, line3));
    }
}