package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.AlreadyExistLineStationException;
import nextstep.subway.line.domain.exceptions.NotRegisteredLineStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class LineStations {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", nullable = false)
    @OrderBy("preStation asc")
    private final List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public List<LineStation> getLineStationsInOrder() {
        Optional<LineStation> firstLineStationOptional = lineStations.stream()
                .filter(lineStation -> lineStation.getPreStation() == null)
                .findFirst();

        if (!firstLineStationOptional.isPresent()) {
            return lineStations;
        }

        LineStation firstLineStation = firstLineStationOptional.get();

        List<LineStation> orderedLineStations = new ArrayList<>();
        orderedLineStations.add(firstLineStation);

        LineStation temp = firstLineStation;
        boolean hasNext = true;
        while (hasNext) {
            hasNext = false;

            for (LineStation lineStation : lineStations) {
                if (lineStation.getPreStation() == temp.getStation()) {
                    orderedLineStations.add(lineStation);
                    temp = lineStation;
                    hasNext = true;
                }
            }
        }

        return orderedLineStations;
    }

    private boolean hasStation(Station station) {
        return this.lineStations.stream()
                .anyMatch(lineStation -> lineStation.getStation() == station);
    }

    public void registerLineStation(LineStation lineStation) {
        if (this.hasStation(lineStation.getStation())) {
            throw new AlreadyExistLineStationException("line station already exists : " + lineStation.getStation().getId());
        }

        lineStations.forEach(it -> {
            Station prevPreStation = it.getPreStation();
            Station newPreStation = lineStation.getPreStation();

            if (prevPreStation != null && prevPreStation.equals(newPreStation)) {
                it.changePreStation(lineStation.getStation());
            }
        });

        this.lineStations.add(lineStation);
    }

    public void excludeLineStation(Station station) {
        if (this.lineStations.size() <= 1) {
            this.lineStations.clear();
        }

        LineStation excludeTarget = this.lineStations.stream()
                .filter(lineStation -> station.equals(lineStation.getStation()))
                .findFirst()
                .orElseThrow(NotRegisteredLineStationException::new);

        Station preStation = excludeTarget.getPreStation();

        if (preStation != null) {
            Optional<LineStation> nextLineStationOptional = this.lineStations.stream()
                    .filter(lineStation -> excludeTarget.getStation().equals(lineStation.getPreStation()))
                    .findFirst();

            nextLineStationOptional.ifPresent(lineStation -> lineStation.changePreStation(preStation));
        }

        this.lineStations.remove(excludeTarget);
    }
}
